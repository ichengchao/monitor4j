/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.jmonitor.monitor4j.client.protocal.message.GetAttribute;
import cn.jmonitor.monitor4j.common.JmonitorConstants;
import cn.jmonitor.monitor4j.plugin.ip.WebIPDataManager;
import cn.jmonitor.monitor4j.plugin.jvm.JVMGC;
import cn.jmonitor.monitor4j.plugin.jvm.JVMInfo;
import cn.jmonitor.monitor4j.plugin.jvm.JVMMemory;
import cn.jmonitor.monitor4j.plugin.jvm.JVMThread;
import cn.jmonitor.monitor4j.plugin.log4j.Log4jDataManager;
import cn.jmonitor.monitor4j.plugin.spring.SpringMethodDataManager;
import cn.jmonitor.monitor4j.plugin.web.WebUrlDataManager;
import cn.jmonitor.monitor4j.plugin.web.profile.WebUrlProfileDataManager;
import cn.jmonitor.monitor4j.utils.FileUtils;
import cn.jmonitor.monitor4j.utils.StringUtils;

/**
 * @author charles 2013年12月20日 上午10:57:23
 */
public class JMXUtils {

    private final static Log LOG = LogFactory.getLog(JMXUtils.class);

    private static MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

    private final static String JmxListAttributeName = "JmonitorDataList";
//    private final static String JmxListAttributeName_Druid_Sql = "SqlList";
//    private final static String JmxListAttributeName_Druid_Datasource = "DataSourceList";

    public static void regMbean() throws Exception {
        unregMbean();
        ObjectName jvmInfoName = new ObjectName(JmonitorConstants.JMX_JVM_INFO_NAME);
        ObjectName jvmMemoryName = new ObjectName(JmonitorConstants.JMX_JVM_MEMORY_NAME);
        ObjectName jvmGCName = new ObjectName(JmonitorConstants.JMX_JVM_GC_NAME);
        ObjectName jvmThreadName = new ObjectName(JmonitorConstants.JMX_JVM_THREAD_NAME);
        ObjectName exceptionName = new ObjectName(JmonitorConstants.JMX_EXCEPTION_NAME);
        ObjectName springMethodName = new ObjectName(JmonitorConstants.JMX_SPRING_METHOD_NAME);
        ObjectName webUrlName = new ObjectName(JmonitorConstants.JMX_WEB_URL_NAME);
        ObjectName webUrlProfileName = new ObjectName(JmonitorConstants.JMX_WEB_URL_PROFILE_NAME);
        ObjectName webIPName = new ObjectName(JmonitorConstants.JMX_WEB_IP_NAME);
//        ObjectName sqlName = new ObjectName(JmonitorConstants.JMX_SQL_NAME);
        mbeanServer.registerMBean(JVMInfo.getInstance(), jvmInfoName);
        mbeanServer.registerMBean(JVMMemory.getInstance(), jvmMemoryName);
        mbeanServer.registerMBean(JVMGC.getInstance(), jvmGCName);
        mbeanServer.registerMBean(JVMThread.getInstance(), jvmThreadName);
        mbeanServer.registerMBean(Log4jDataManager.getInstance(), exceptionName);
        mbeanServer.registerMBean(SpringMethodDataManager.getInstance(), springMethodName);
        mbeanServer.registerMBean(WebUrlDataManager.getInstance(), webUrlName);
        mbeanServer.registerMBean(WebUrlProfileDataManager.getInstance(), webUrlProfileName);
        mbeanServer.registerMBean(WebIPDataManager.getInstance(), webIPName);

        FileUtils.appendToLog("reg mbeans success.");
    }

    public static void unregMbean() throws Exception {
        ObjectName jvmInfoName = new ObjectName(JmonitorConstants.JMX_JVM_INFO_NAME);
        ObjectName jvmMemoryName = new ObjectName(JmonitorConstants.JMX_JVM_MEMORY_NAME);
        ObjectName jvmGCName = new ObjectName(JmonitorConstants.JMX_JVM_GC_NAME);
        ObjectName jvmThreadName = new ObjectName(JmonitorConstants.JMX_JVM_THREAD_NAME);
        ObjectName exceptionName = new ObjectName(JmonitorConstants.JMX_EXCEPTION_NAME);
        ObjectName springMethodName = new ObjectName(JmonitorConstants.JMX_SPRING_METHOD_NAME);
        ObjectName webUrlName = new ObjectName(JmonitorConstants.JMX_WEB_URL_NAME);
        ObjectName webUrlProfileName = new ObjectName(JmonitorConstants.JMX_WEB_URL_PROFILE_NAME);
        ObjectName webIPName = new ObjectName(JmonitorConstants.JMX_WEB_IP_NAME);
        ObjectName sqlName = new ObjectName(JmonitorConstants.JMX_SQL_NAME);
        List<ObjectName> objectNameList = new ArrayList<ObjectName>();
        objectNameList.add(jvmInfoName);
        objectNameList.add(jvmMemoryName);
        objectNameList.add(jvmGCName);
        objectNameList.add(jvmThreadName);
        objectNameList.add(exceptionName);
        objectNameList.add(springMethodName);
        objectNameList.add(webUrlName);
        objectNameList.add(webUrlProfileName);
        objectNameList.add(webIPName);
        objectNameList.add(sqlName);
        for (ObjectName objectName : objectNameList) {
            if (mbeanServer.isRegistered(objectName)) {
                mbeanServer.unregisterMBean(objectName);
            }
        }
    }

    /**
     * 标准的采集方式,暂时不对外暴露
     * 
     * @param getAttribute
     * @return
     */
    private static Map<String, Object> getAttribute(GetAttribute getAttribute) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null == getAttribute || StringUtils.isBlank(getAttribute.getObjectName())
                || null == getAttribute.getAttributeNames()) {
            return result;
        }

        try {
            List<String> attributeNames = getAttribute.getAttributeNames();
            List<String> options = getAttribute.getOptions();
            ObjectName objectName = new ObjectName(getAttribute.getObjectName());
            for (String attributeName : attributeNames) {
                // 这里单个属性的失败不影响其他数据采集
                try {
                    Object value = mbeanServer.getAttribute(objectName, attributeName);
                    if (value instanceof TabularDataSupport) {
                        List<Object> list = new ArrayList<Object>();
                        TabularDataSupport tabularDataSupport = (TabularDataSupport) value;
                        for (Object itemValue : tabularDataSupport.values()) {
                            if (itemValue instanceof CompositeData) {
                                CompositeData compositeData = (CompositeData) itemValue;
                                Map<String, Object> singleLine = new HashMap<String, Object>();
                                for (String key : compositeData.getCompositeType().keySet()) {
                                    Object entryValue = compositeData.get(key);
                                    singleLine.put(key, entryValue);
                                }
                                list.add(singleLine);
                            }
                        }
                        value = list;
                    }
                    result.put(attributeName, value);
                } catch (Exception e) {
                    // LOG.error(e.getMessage(), e);
                }
            }
            if (null != options) {
                for (String option : options) {
                    if ("reset=true".equals(option)) {
                        mbeanServer.invoke(objectName, "reset", null, null);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    /**
     * 格式化<br>
     * 只支持单行或者多行的数据,如果是多行直接返回数组
     * 
     * @param getAttribute
     * @return
     */
    public static Object getAttributeFormatted(GetAttribute getAttribute) {
        Map<String, Object> map = getAttribute(getAttribute);
//        // druid datasource的特殊处理
//        if (JmonitorConstants.JMX_DRUID_DATASOURCE_NAME.equals(getAttribute.getObjectName())) {
//            return map.get(JmxListAttributeName_Druid_Datasource);
//        }
//        // druid sql的特殊处理
//        if (JmonitorConstants.JMX_SQL_NAME.equals(getAttribute.getObjectName())) {
//            return map.get(JmxListAttributeName_Druid_Sql);
//        }
        // 默认的多行数据的名称为"JmonitorDataList"
        if (null != map.get(JmxListAttributeName)) {
            return map.get(JmxListAttributeName);
        }
        return map;
    }
}
