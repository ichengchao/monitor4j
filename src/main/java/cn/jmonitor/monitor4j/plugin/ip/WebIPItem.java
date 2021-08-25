/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.ip;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * 单个url的统计信息
 * 
 * @author charles 2013年11月27日 上午11:26:36
 */
public class WebIPItem {

    private String ip;

    public WebIPItem(String ip) {
        this.ip = ip;
    }

    private final AtomicInteger runningCount = new AtomicInteger();
    // 最大并发
    private final AtomicInteger concurrentMax = new AtomicInteger();

    // 总数
    private final AtomicLong count = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong();

    // 总耗时
    private final AtomicLong nanoTotal = new AtomicLong(0);
    // 最大耗时
    private final AtomicLong nanoMax = new AtomicLong(0);

    private String lastErrorMessage = "";
    private Date lastErrorTime;

    public void incrementRunningCount() {
        runningCount.incrementAndGet();
        count.incrementAndGet();
    }

    // 把变量的变化控制在类内
    public void handleAfter(long startNano, boolean error, String errorMsg) {
        // 最大并发计算
        int runningCountNow = getRunningCount();
        int concurrentMaxNow = getConcurrentMax();
        if (runningCountNow > concurrentMaxNow) {
            concurrentMax.compareAndSet(concurrentMaxNow, runningCountNow);
        }
        // 最耗时计算
        long nanoUseNow = System.nanoTime() - startNano;
        long nanoMaxNow = getNanoMax();
        if (nanoUseNow > nanoMaxNow) {
            nanoMax.compareAndSet(nanoMaxNow, nanoUseNow);
        }
        // 总耗时,总数
        runningCount.decrementAndGet();
        nanoTotal.addAndGet(nanoUseNow);
        if (error) {
            errorCount.incrementAndGet();
            lastErrorMessage = errorMsg;
            lastErrorTime = new Date();
        }
    }

    public int getRunningCount() {
        return runningCount.get();
    }

    public int getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getCount() {
        return count.get();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public long getNanoTotal() {
        return nanoTotal.get();
    }

    public long getNanoMax() {
        return nanoMax.get();
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public Date getLastErrorTime() {
        return lastErrorTime;
    }

    public String getIp() {
        return ip;
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {
        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }
        OpenType<?>[] indexTypes = new OpenType<?>[] {
                //
                SimpleType.STRING, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.LONG, //
                SimpleType.LONG, //

                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.STRING, //
                SimpleType.DATE, //
        };
        String[] indexNames = {
                //
                "IP", //
                "RunningCount", //
                "ConcurrentMax", //
                "Count", //
                "ErrorCount", //

                "NanoTotal", //
                "NanoMax", //
                "LastErrorMessage", //
                "LastErrorTime", //
        };
        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("IPStatistic", "IP Statistic", indexNames, indexDescriptions, indexTypes);
        return COMPOSITE_TYPE;
    }

    public CompositeData getCompositeData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("IP", getIp());
        map.put("RunningCount", getRunningCount());
        map.put("ConcurrentMax", getConcurrentMax());
        map.put("Count", getCount());
        map.put("ErrorCount", getErrorCount());
        map.put("NanoTotal", getNanoTotal());
        map.put("NanoMax", getNanoMax());
        map.put("LastErrorMessage", getLastErrorMessage());
        map.put("LastErrorTime", getLastErrorTime());

        return new CompositeDataSupport(getCompositeType(), map);
    }

}
