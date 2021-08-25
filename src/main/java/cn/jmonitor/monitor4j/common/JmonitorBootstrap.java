package cn.jmonitor.monitor4j.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.jmonitor.monitor4j.VERSION;
import cn.jmonitor.monitor4j.client.JmonitorClient;
import cn.jmonitor.monitor4j.jmx.JMXUtils;
import cn.jmonitor.monitor4j.utils.FileUtils;
import cn.jmonitor.monitor4j.utils.StringUtils;

/**
 * 启动类
 * 
 * @author charles-dell 2014-1-3 下午01:37:30
 */
public class JmonitorBootstrap {

    private static Log LOG = LogFactory.getLog(JmonitorBootstrap.class);

    private static JmonitorBootstrap instance = new JmonitorBootstrap();

    private static boolean isStart = false;
    private static boolean isStop = false;

    private JmonitorBootstrap() {
    }

    private JmonitorClient client;
    private String agentHost;
    private String agentPort;
    private String appNum;
    private String enableMonitorIp;
    private String enableDruidFilter;

    public static JmonitorBootstrap getInstance() {
        return instance;
    }

    public static boolean isStart() {
        return isStart;
    }

    public static boolean isStop() {
        return isStop;
    }

    public synchronized void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        FileUtils.appendToLog("monitor4j version:" + VERSION.VERSION);
        try {
            JMXUtils.regMbean();
            // 是否开启webIP的监控
            if (StringUtils.isNotBlank(enableMonitorIp)) {
                JmonitorConstants.enableMonitorIp = Boolean.valueOf(enableMonitorIp);
            }
            if (StringUtils.isNotBlank(enableDruidFilter)) {
                JmonitorConstants.enableDruidFilter = Boolean.valueOf(enableDruidFilter);
            }
            if (null == client) {
                client = new JmonitorClient();
                if (null != agentHost) {
                    client.setAgentHost(agentHost);
                }
                if (null != agentPort) {
                    client.setAgentPort(Integer.valueOf(agentPort));
                }
                if (null != appNum) {
                    client.setAppNum(appNum);
                }
                client.start();
                FileUtils.appendToLog("client start over.");
            }
        } catch (Exception e) {
            FileUtils.appendToLog("client start error:" + e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    public synchronized void stop() {
        if (isStop) {
            return;
        }
        isStop = true;
        try {
            JMXUtils.unregMbean();
            if (null != client) {
                client.stop();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public void setAgentPort(String agentPort) {
        this.agentPort = agentPort;
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public void setEnableMonitorIp(String enableMonitorIp) {
        this.enableMonitorIp = enableMonitorIp;
    }

    public void setEnableDruidFilter(String enableDruidFilter) {
        this.enableDruidFilter = enableDruidFilter;
    }

}
