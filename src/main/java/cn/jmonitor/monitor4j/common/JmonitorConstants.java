/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.common;

/**
 * @author charles 2013年12月23日 下午4:38:26
 */
public class JmonitorConstants {

    public final static String VERSION = "1.0";
    public final static String charset = "UTF-8";

    public final static String newLine = "\n";

    public final static String MSG_CONNECT = "Connect";
    public final static String MSG_CONNECT_RESP = "ConnectResp";
    public final static String MSG_GETATTRIBUTE = "GetAttribute";
    public final static String MSG_GETATTRIBUTE_RESP = "GetAttributeResp";
    public final static String MSG_HEARTBEAT = "Heartbeat";
    public final static String MSG_TS = "TS";
    public final static String MSG_S = "S";
    public final static String MSG_T = "T";
    public final static String MSG_VAL = "VAL";
    public final static String MSG_ERROR = "ERROR";

    // 不兼容dragoon的命名规则
    public final static String JMX_JVM_INFO_NAME = "cn.jmonitor.monitor4j:type=JVMInfo";
    public final static String JMX_JVM_MEMORY_NAME = "cn.jmonitor.monitor4j:type=JVMMemory";
    public final static String JMX_JVM_GC_NAME = "cn.jmonitor.monitor4j:type=JVMGC";
    public final static String JMX_JVM_THREAD_NAME = "cn.jmonitor.monitor4j:type=JVMThread";
    public final static String JMX_EXCEPTION_NAME = "cn.jmonitor.monitor4j:type=Exception";
    public final static String JMX_SPRING_METHOD_NAME = "cn.jmonitor.monitor4j:type=SpringMethod";
    public final static String JMX_WEB_URL_NAME = "cn.jmonitor.monitor4j:type=WebUrl";
    public final static String JMX_WEB_URL_PROFILE_NAME = "cn.jmonitor.monitor4j:type=WebUrlProfile";
    public final static String JMX_WEB_IP_NAME = "cn.jmonitor.monitor4j:type=WebIP";
    public final static String JMX_SQL_NAME = "cn.jmonitor.monitor4j:type=DruidSql";

    // 可配置的参数名称

    public final static String JMONITOR_AGENTPORT = "Jmonitor_AgentPort";
    public final static String JMONITOR_AGENTHOST = "Jmonitor_AgentHost";
    public final static String JMONITOR_ENABLE_MONITOR_IP = "Jmonitor_Enable_Monitor_IP";
    public final static String JMONITOR_ENABLE_DRUID_FILTER = "Jmonitor_Enable_Druid_Filter";
    // 兼容dragoon
    public final static String JMONITOR_APPNUM = "APP_NUM";

    public static boolean enableMonitorIp = false;
    public static boolean enableDruidFilter = true;

}
