package cn.jmonitor.monitor4j.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import cn.jmonitor.monitor4j.client.protocal.message.BaseMessage;
import cn.jmonitor.monitor4j.client.protocal.message.GetAttribute;
import cn.jmonitor.monitor4j.client.protocal.tlv.TLVMessage;

/**
 * 类AgentServerMock.java的实现描述：模拟agent
 * 
 * @author charles-dell 2013-12-24 下午01:03:26
 */
public class AgentServerMock {

    private final static Log LOG = LogFactory.getLog(AgentServerMock.class);
    public final static short TEXT_UTF_8 = 1;
    private static BlockingQueue<BaseMessage> sendQueue = new ArrayBlockingQueue<BaseMessage>(100);

    private Object writerLock = new Object();
    private Object closeLock = new Object();
    private final static int AGENT_PORT = 19777;

    public static void main1(String[] args) {
        String line = "jmx://GetAttr/cn.jmonitor.monitor4j:type=JVMMemory(\"MemoryHeapMax\",\"MemoryHeapMax11\")";
        line = line.substring("jmx://GetAttr/".length());
        GetAttribute getAttribute = new GetAttribute();
        getAttribute.setObjectName(line.substring(0, line.indexOf('(')));
        String attributeNames = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        List<String> attrList = new ArrayList<String>();
        for (String s : attributeNames.split(",")) {
            attrList.add(s.substring(1, s.length() - 1));
        }
        getAttribute.setAttributeNames(attrList);
        int indexOfOption = line.indexOf('?');
        if (indexOfOption != -1) {
            List<String> optionList = new ArrayList<String>();
            String option = line.substring(indexOfOption + 1);
            for (String optionItem : option.split("&")) {
                optionList.add(optionItem);
            }
            getAttribute.setOptions(optionList);
        }

        System.out.println(getAttribute.getObjectName());
        System.out.println(getAttribute.getAttributeNames());
        System.out.println(getAttribute.getOptions());
    }

    // jmx://GetAttr/cn.jmonitor.monitor4j:type=JVMMemory("MemoryHeapMax","MemoryHeapUsed")
    // jmx://GetAttr/cn.jmonitor.monitor4j:type=Exception("ErrorList")?reset=true
    // error:
    // jmx://GetAttr/cn.jmonitor.monitor4j:type=JVMMemory("MemoryHeapMax","test")
    public static void main(String[] args) throws Exception {
        // mac
        // PropertyConfigurator.configure("/Users/charles/java/log4j.properties");
        PropertyConfigurator.configure("D:/log4j.properties");
        // 从控制台输入
        new Thread(new Runnable() {

            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    try {
                        String line = scanner.nextLine();
                        line = line.substring("jmx://GetAttr/".length());
                        GetAttribute getAttribute = new GetAttribute();
                        getAttribute.setObjectName(line.substring(0, line.indexOf('(')));
                        String attributeNames = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                        List<String> attrList = new ArrayList<String>();
                        for (String s : attributeNames.split(",")) {
                            attrList.add(s.substring(1, s.length() - 1));
                        }
                        getAttribute.setAttributeNames(attrList);
                        int indexOfOption = line.indexOf('?');
                        if (indexOfOption != -1) {
                            List<String> optionList = new ArrayList<String>();
                            String option = line.substring(indexOfOption + 1);
                            for (String optionItem : option.split("&")) {
                                optionList.add(optionItem);
                            }
                            getAttribute.setOptions(optionList);
                        }
                        sendQueue.offer(getAttribute);
                    } catch (Exception e) {
                        scanner.close();
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
        AgentServerMock agentServerMock = new AgentServerMock();
        agentServerMock.start();
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(AGENT_PORT);
        System.out.println("agent server started,port:" + AGENT_PORT);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                LOG.info("client connect:" + socket.getPort());
                handleSocket(socket);
            } catch (Exception e) {
                serverSocket.close();
            }
        }
    }

    public void close(DataOutputStream writer, DataInputStream reader, Socket socket) {
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

    public void handleSocket(final Socket socket) throws IOException {
        final DataInputStream reader = new DataInputStream(socket.getInputStream());
        final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

        // reader thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (null == reader) {
                            break;
                        }
                        short type = reader.readShort();
                        int length = reader.readInt();
                        byte[] value = new byte[length];
                        reader.readFully(value);
                        TLVMessage tlvMessage = new TLVMessage(type, length, value);
                        // print receive data
                        LOG.info("reader:" + new String(tlvMessage.getValue(), "UTF-8"));
                    } catch (Exception e) {
                        close(writer, reader, socket);
                        LOG.error(e.getMessage(), e);
                        break;
                    }

                }
            }
        }).start();

        // writer thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (null == writer) {
                            break;
                        }
                        BaseMessage message = sendQueue.take();
                        sendMsgDirect(writer, message);
                        LOG.info("writer:" + message.buildMsg());
                    } catch (Exception e) {
                        close(writer, reader, socket);
                        LOG.error(e.getMessage(), e);
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMsgDirect(DataOutputStream writer, BaseMessage message) throws IOException {
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
            } catch (IOException ex) {
                throw ex;
            }
        }

    }

}
