/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.log4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

/**
 * @author charles 2013年12月20日 下午3:31:54
 */
public class Log4jDataManager implements Log4jDataManagerMBean {

    private static Log4jDataManager instance = new Log4jDataManager();

    private final AtomicLong resetCount = new AtomicLong();

    private final ConcurrentMap<LogItemKey, LogItemValue> logMap = new ConcurrentHashMap<LogItemKey, LogItemValue>();

    private String logConfig;

    public static final Log4jDataManager getInstance() {
        return instance;
    }

    private Log4jDataManager() {
        // never
    }

    @Override
    public void reset() {
        resetCount.incrementAndGet();
        logMap.clear();
    }

    @Override
    public long getResetCount() {
        return resetCount.get();
    }

    @Override
    public TabularData getJmonitorDataList() throws JMException {
        CompositeType rowType = LogItemValue.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);
        TabularType tabularType = new TabularType("ErrorList", "ErrorList", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);
        for (LogItemValue logItemValue : logMap.values()) {
            data.put(logItemValue.getCompositeData());
        }
        return data;
    }

    public ConcurrentMap<LogItemKey, LogItemValue> getLogMap() {
        return logMap;
    }

    @Override
    public String getLogConfig() {
        return this.logConfig;
    }

    public void setLogConfig(String logConfig) {
        this.logConfig = logConfig;
    }

}
