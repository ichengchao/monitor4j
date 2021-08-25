/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.log4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * @author charles 2013年12月4日 下午1:57:58
 */
public class LogItemValue {

    private final AtomicLong count = new AtomicLong();
    private LogItemKey logItemKey;
    private Date lastDate;
    private String lastMessage;
    private String lastStackTrace;

    public LogItemValue(LogItemKey logItemKey) {
        this.logItemKey = logItemKey;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastStackTrace() {
        return lastStackTrace;
    }

    public void setLastStackTrace(String lastStackTrace) {
        this.lastStackTrace = lastStackTrace;
    }

    public long getCount() {
        return count.get();
    }

    public void incrementCount() {
        lastDate = new Date();
        count.incrementAndGet();
    }

    public CompositeDataSupport getCompositeData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("Type", logItemKey.getExceptionType());
        map.put("Method", logItemKey.getMethodName());
        map.put("StackTrace", getLastStackTrace());
        map.put("Count", getCount());
        map.put("LastThrowDate", getLastDate());
        map.put("LastThrowMessage", getLastMessage());

        return new CompositeDataSupport(getCompositeType(), map);
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {
        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }
        OpenType<?>[] indexTypes = new OpenType<?>[] {
                //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.STRING //
        };
        String[] indexNames = {
                //
                "Type", //
                "Method", //
                "StackTrace", //
                "Count", //
                "LastThrowDate", //
                "LastThrowMessage" //
        };
        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("ExceptionStatistic", "Exception Statistic", indexNames, indexDescriptions,
                indexTypes);
        return COMPOSITE_TYPE;
    }

}
