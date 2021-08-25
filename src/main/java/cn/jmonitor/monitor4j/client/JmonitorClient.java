package cn.jmonitor.monitor4j.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import cn.jmonitor.monitor4j.client.protocal.message.BaseMessage;
import cn.jmonitor.monitor4j.client.protocal.message.Connect;
import cn.jmonitor.monitor4j.client.protocal.message.GetAttribute;
import cn.jmonitor.monitor4j.client.protocal.message.GetAttributeResp;
import cn.jmonitor.monitor4j.client.protocal.message.Heartbeat;
import cn.jmonitor.monitor4j.client.protocal.tlv.TLVMessage;
import cn.jmonitor.monitor4j.common.JmonitorConstants;
import cn.jmonitor.monitor4j.jmx.JMXUtils;
import cn.jmonitor.monitor4j.utils.DaemonThreadFactory;
import cn.jmonitor.monitor4j.utils.FileUtils;
import cn.jmonitor.monitor4j.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 用于发送,接收数据<br>
 * 有心跳,而且能自动重连
 * 
 * @author charles-dell 2013-12-22 下午01:46:38
 */
public class JmonitorClient {

    public static void main(String[] args) throws Exception {
        // mac
        // PropertyConfigurator.configure("/Users/charles/java/log4j.properties");
        PropertyConfigurator.configure("D:/log4j.properties");
        JMXUtils.regMbean();
        JmonitorClient client = new JmonitorClient();
        client.start();
        Thread.sleep(Long.MAX_VALUE);
    }

    private final static Log LOG = LogFactory.getLog(JmonitorClient.class);
    // public final static short RAW_BINARY = 0;
    private final static short TEXT_UTF_8 = 1;
    private ScheduledFuture<?> heartbeatFuture = null;
    private ScheduledFuture<?> checkFuture = null;
    private long heartbeatPeriod = 60;
    private long checkPeriod = 60;
    private final int maxSendCount = 100;

    private int agentPort = 19777;
    private String agentHost = "127.0.0.1";
    private String appNum;

    private AtomicInteger clientConnectErrorCount = new AtomicInteger(0);

    private BlockingQueue<BaseMessage> sendMsgQueue = new ArrayBlockingQueue<BaseMessage>(maxSendCount);

    private ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1,
            new DaemonThreadFactory("JmonitorClient-HeartBeat"));

    private ScheduledExecutorService checkScheduler = Executors.newScheduledThreadPool(1,
            new DaemonThreadFactory("JmonitorClient-CheckThread"));

    private ExecutorService writerExcutorService = Executors
            .newSingleThreadExecutor(new DaemonThreadFactory("JmonitorClient-WriterThread"));

    private ExecutorService readerExcutorService = Executors
            .newSingleThreadExecutor(new DaemonThreadFactory("JmonitorClient-ReaderThread"));

    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    private Object writerLock = new Object();
    private Object closeLock = new Object();

    public void start() {
        FileUtils.appendToLog("client agentHost:" + agentHost);
        FileUtils.appendToLog("client agentPort:" + agentPort);
        FileUtils.appendToLog("client appNum:" + appNum);
        // 开启检查线程
        checkFuture = checkScheduler.scheduleWithFixedDelay(new CheckTask(), 0, checkPeriod, TimeUnit.SECONDS);
        // 开启心跳
        heartbeatFuture = heartbeatScheduler.scheduleAtFixedRate(new HeartTask(), 0, heartbeatPeriod, TimeUnit.SECONDS);
        // 开启读线程
        readerExcutorService.execute(new readerTask());
        // 开启写线程
        writerExcutorService.execute(new writerTask());
    }

    public void stop() {
        if (heartbeatFuture != null) {
            if (heartbeatFuture.cancel(true)) {
                heartbeatFuture = null;
            }
        }

        if (checkFuture != null) {
            if (checkFuture.cancel(true)) {
                checkFuture = null;
            }
        }
        try {
            heartbeatScheduler.shutdownNow();
        } catch (Exception e) {
        }
        try {
            checkScheduler.shutdownNow();
        } catch (Exception e) {
        }
        try {
            writerExcutorService.shutdownNow();
        } catch (Exception e) {
        }
        try {
            readerExcutorService.shutdownNow();
        } catch (Exception e) {
        }
    }

    // 出现异常的时候调用此方法
    private void close() {
        synchronized (closeLock) {
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
                if (writer != null) {
                    writer.close();
                    writer = null;
                }
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void handleTLVMessage(TLVMessage tlvMessage) {
        try {
            if (null == tlvMessage) {
                return;
            }
            if (TEXT_UTF_8 != tlvMessage.getType()) {
                return;
            }
            String text = new String(tlvMessage.getValue(), JmonitorConstants.charset);
            if (LOG.isDebugEnabled()) {
                LOG.debug("read:" + text);
            }
            JSONArray array = JSONArray.parseArray(text);
            JSONObject header = array.getJSONObject(0);
            JSONObject body = array.getJSONObject(1);
            String type = header.getString(JmonitorConstants.MSG_T);
            Object msg = null;
            // GetAttribute
            if (JmonitorConstants.MSG_GETATTRIBUTE.equals(type)) {
                GetAttribute request = JSON.toJavaObject(body, GetAttribute.class);
                request.setSequence(header.getIntValue(JmonitorConstants.MSG_S));
                msg = request;
            } else if (JmonitorConstants.MSG_CONNECT_RESP.equals(type)) {
                // ConnectResp
                String error = body.getString("ERROR");
                if (StringUtils.isNotBlank(error)) {
                    clientConnectErrorCount.incrementAndGet();
                }
            }
            // 其他协议可以在这里扩展
            if (null == msg) {
                return;
            }
            BaseMessage respMsg = handleMessage(msg);
            if (null == respMsg) {
                return;
            }
            sendMsg(respMsg);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    private BaseMessage handleMessage(Object object) {
        if (null == object) {
            return null;
        }
        if (object instanceof GetAttribute) {
            GetAttribute getAttribute = (GetAttribute) object;
            Object valueMap = JMXUtils.getAttributeFormatted(getAttribute);
            GetAttributeResp attributeResp = new GetAttributeResp(valueMap);
            attributeResp.setAgentSequence(getAttribute.getSequence());
            return attributeResp;
        }
        return null;
    }

    private final class HeartTask implements Runnable {

        public void run() {
            try {
                sendMsgDirect(new Heartbeat());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private final class CheckTask implements Runnable {

        public void run() {
            try {
                if (null == socket) {
                    socket = new Socket(agentHost, agentPort);
                    // 在agent没有监听19777的时候,可能会导致localport为19777
                    if (socket.getLocalPort() == agentPort) {
                        close();
                        return;
                    }
                    reader = new DataInputStream(socket.getInputStream());
                    writer = new DataOutputStream(socket.getOutputStream());
                    LOG.info("jmonitor connect agent success,localPort:" + socket.getLocalPort());
                    FileUtils.appendToLog("jmonitor connect agent success,localPort:" + socket.getLocalPort());
                    // 发送connect消息
                    sendConnectMsg();
                }
            } catch (Exception e) {
                FileUtils.appendToLog("jmonitor connect error:" + e.getMessage());
                close();
                LOG.error(e.getMessage());
            }
        }
    }

    private final class readerTask implements Runnable {

        public void run() {
            while (true) {
                try {
                    if (Thread.interrupted()) {
                        break;
                    }
                    if (null == reader) {
                        Thread.sleep(1000);
                        continue;
                    }
                    short type = reader.readShort();
                    int length = reader.readInt();
                    byte[] value = new byte[length];
                    reader.readFully(value);
                    TLVMessage tlvMessage = new TLVMessage(type, length, value);
                    handleTLVMessage(tlvMessage);
                } catch (IOException e) {
                    close();
                    LOG.warn(e.getMessage(), e);
                } catch (InterruptedException e) {
                    close();
                    LOG.warn(e.getMessage(), e);
                    break;
                } catch (Exception e) {
                    close();
                    LOG.warn(e.getMessage(), e);
                }

            }
        }
    }

    private final class writerTask implements Runnable {

        public void run() {
            while (true) {
                try {
                    if (Thread.interrupted()) {
                        break;
                    }
                    if (null == writer) {
                        Thread.sleep(1000);
                        continue;
                    }
                    // 如果写入失败,会丢失数据
                    BaseMessage message = sendMsgQueue.take();
                    if (null == message) {
                        continue;
                    }
                    sendMsgDirect(message);
                } catch (IOException e) {
                    close();
                    LOG.warn(e.getMessage(), e);
                } catch (InterruptedException e) {
                    close();
                    LOG.warn(e.getMessage(), e);
                    break;
                } catch (Exception e) {
                    close();
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
    }

    private boolean sendMsg(BaseMessage message) {
        boolean isSuccess = false;
        if (null == message) {
            return isSuccess;
        }
        try {
            if (sendMsgQueue.size() >= maxSendCount) {
                // 丢弃老的数据
                sendMsgQueue.poll(1, TimeUnit.SECONDS);
            }
            isSuccess = sendMsgQueue.offer(message, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn(e.getMessage(), e);
        }
        return isSuccess;
    }

    private void sendMsgDirect(BaseMessage message) throws IOException {
        synchronized (writerLock) {
            try {
                if (null == writer) {
                    return;
                }
                byte[] bytes = message.buildMsgByte();
                writer.writeShort(TEXT_UTF_8);
                writer.writeInt(bytes.length);
                writer.write(bytes);
                writer.flush();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("send:" + message.buildMsg());
                }
            } catch (IOException ex) {
                close();
                throw ex;
            }
        }

    }

    private void sendConnectMsg() throws IOException {
        Connect connectMsg = new Connect(appNum);
        sendMsgDirect(connectMsg);
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
    }
}
