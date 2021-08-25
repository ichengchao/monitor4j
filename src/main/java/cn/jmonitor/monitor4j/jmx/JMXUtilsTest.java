/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import cn.jmonitor.monitor4j.client.protocal.message.GetAttribute;
import cn.jmonitor.monitor4j.client.protocal.message.GetAttributeResp;
import cn.jmonitor.monitor4j.common.JmonitorConstants;

/**
 * @author charles 2013年12月20日 上午10:57:23
 */
public class JMXUtilsTest {

    private final static Log LOG = LogFactory.getLog(JMXUtilsTest.class);

    @SuppressWarnings("unchecked")
    public static void getJVMInfo() throws Exception {
        GetAttribute getAttribute = new GetAttribute();
        getAttribute.setObjectName(JmonitorConstants.JMX_JVM_INFO_NAME);
        List<String> attributeNames = new ArrayList<String>();
        attributeNames.add("StartTime");
        attributeNames.add("JVM");
        attributeNames.add("JavaVersion");
        attributeNames.add("PID");
        attributeNames.add("InputArguments");
        attributeNames.add("JavaHome");
        attributeNames.add("Arch");
        attributeNames.add("OSName");
        attributeNames.add("OSVersion");
        attributeNames.add("JavaSpecificationVersion");
        attributeNames.add("JavaLibraryPath");
        attributeNames.add("AvailableProcessors");
        attributeNames.add("LoadedClassCount");
        attributeNames.add("TotalLoadedClassCount");
        attributeNames.add("UnloadedClassCount");
        attributeNames.add("TotalCompilationTime");
        attributeNames.add("FileEncode");
        getAttribute.setAttributeNames(attributeNames);
        Map<String, Object> result = (Map<String, Object>) JMXUtils.getAttributeFormatted(getAttribute);
        GetAttributeResp attributeResp = new GetAttributeResp(result);
        System.out.println("JVMInfo:");
        System.out.println(attributeResp.buildMsg());
    }

    @SuppressWarnings("unchecked")
    public static void getException() throws Exception {
        GetAttribute getAttribute = new GetAttribute();
        getAttribute.setObjectName(JmonitorConstants.JMX_EXCEPTION_NAME);
        List<String> attributeNames = new ArrayList<String>();
        attributeNames.add("ErrorList");
        getAttribute.setAttributeNames(attributeNames);
        Map<String, Object> result = (Map<String, Object>) JMXUtils.getAttributeFormatted(getAttribute);
        GetAttributeResp attributeResp = new GetAttributeResp(result);
        System.out.println("Exception:");
        System.out.println(attributeResp.buildMsg());
    }

    public static void logError1() {
        LOG.error("error info 1");
        LOG.error("error info 1");
    }

    public static void logError2() {
        LOG.error("error info 2");
        LOG.error("error info 2");
        LOG.error("error info 2");
    }

    public static void getAllInfo() throws Exception {
        PropertyConfigurator.configure("D:/jmonitor_log4j.properties");
        logError1();
        logError2();
        JMXUtils.regMbean();
        getJVMInfo();
        getException();
    }

    public static void main(String[] args) throws Exception {
        getAllInfo();
        Thread.sleep(Long.MAX_VALUE);
    }

}
