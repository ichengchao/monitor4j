/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web;

import cn.jmonitor.monitor4j.common.JmonitorBootstrap;
import cn.jmonitor.monitor4j.common.JmonitorConstants;
import cn.jmonitor.monitor4j.utils.FileUtils;
import cn.jmonitor.monitor4j.utils.StringUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * web启动,通过web容器带动
 * 
 * @author charles 2013年12月30日 下午2:22:29
 */
public class JmonitorServletContextListener implements ServletContextListener {

    // private static Log LOG =
    // LogFactory.getLog(JmonitorServletContextListener.class);

    private JmonitorBootstrap bootstrap = JmonitorBootstrap.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent event) {
        FileUtils.appendToLog("start by web listener.");
        String agentHost = event.getServletContext().getInitParameter(JmonitorConstants.JMONITOR_AGENTHOST);
        String agentPort = event.getServletContext().getInitParameter(JmonitorConstants.JMONITOR_AGENTPORT);
        String appNum = event.getServletContext().getInitParameter(JmonitorConstants.JMONITOR_APPNUM);
        String enableMonitorIp = event.getServletContext()
                .getInitParameter(JmonitorConstants.JMONITOR_ENABLE_MONITOR_IP);
        String enableDruidFilter = event.getServletContext()
                .getInitParameter(JmonitorConstants.JMONITOR_ENABLE_DRUID_FILTER);
        bootstrap.setAgentHost(StringUtils.trimToNull(agentHost));
        bootstrap.setAgentPort(StringUtils.trimToNull(agentPort));
        bootstrap.setAppNum(StringUtils.trimToNull(appNum));
        bootstrap.setEnableMonitorIp(StringUtils.trimToNull(enableMonitorIp));
        bootstrap.setEnableDruidFilter(StringUtils.trimToNull(enableDruidFilter));
        bootstrap.start();
        FileUtils.appendToLog("JmonitorBootstrap[web]:" + bootstrap.toString());
        FileUtils.appendToLog("JmonitorBootstrap[web]'s isStart:" + JmonitorBootstrap.isStart());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        bootstrap.stop();
    }
}
