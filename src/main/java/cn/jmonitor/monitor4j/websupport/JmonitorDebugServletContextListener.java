/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.websupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cn.jmonitor.monitor4j.jmx.JMXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 启动debug模式的数据采集
 * 
 * @author charles 2013年12月30日 下午2:22:29
 */
public class JmonitorDebugServletContextListener implements ServletContextListener {

    private static Log LOG = LogFactory.getLog(JmonitorDebugServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            JMXUtils.regMbean();
            CollectManager.startCollect();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            JMXUtils.unregMbean();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
