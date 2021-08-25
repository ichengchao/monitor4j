/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.kv;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 类KVLogBridger.java的实现描述：把kv数据打到日志,支持kv数据能被采集
 * 
 * @author charles 2014年1月23日 上午11:44:22
 */
public class KVLogBridger {

    public final static Log LOG = LogFactory.getLog(KVLogBridger.class);

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("{yyyy-MM-dd HH:mm:ss}");

    public static void doLog(Map<String, Object> map) {
        if (null == map) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            doLog(entry.getKey(), entry.getValue());
        }

    }

    public static void doLog(String key, Object value) {
        if (null == key || null == value) {
            return;
        }
        LOG.info(dateFormat.format(new Date()) + key + ":" + value.toString());
    }
}
