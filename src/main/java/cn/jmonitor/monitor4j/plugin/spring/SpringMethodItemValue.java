/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.spring;

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
 * @author charles 2013年12月25日 下午1:43:21
 */
public class SpringMethodItemValue {

    private SpringMethodItemKey springMethodItemKey;

    private final AtomicInteger runningCount = new AtomicInteger(0);
    // 最大并发
    private final AtomicInteger concurrentMax = new AtomicInteger(0);

    // 总数
    private final AtomicLong count = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    // 总耗时
    private final AtomicLong nanoTotal = new AtomicLong(0);
    // 最大耗时
    private final AtomicLong nanoMax = new AtomicLong(0);

    private String lastErrorMessage = "";
    private String LastErrorStackTrace = "";
    private Date lastErrorTime;

    public SpringMethodItemValue(SpringMethodItemKey springMethodItemKey) {
        this.springMethodItemKey = springMethodItemKey;
    }

    public void incrementRunningCount() {
        runningCount.incrementAndGet();
        // 需要在handleBefore加上count,否则会出现采集结果中count=0的情况,已经在getJmonitorDataList中做判断
        // count.incrementAndGet();
    }

    // 把变量的变化控制在类内
    public void handleAfter(long startNano, boolean error, String errorMsg, String errorSackTrace) {
        count.incrementAndGet();

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
            LastErrorStackTrace = errorSackTrace;
            lastErrorTime = new Date();
        }
    }

    // 清除数据,为了解决方法执行跨采集周期的问题
    public void cleardata() {
        concurrentMax.set(getRunningCount());
        count.set(0);
        errorCount.set(0);
        nanoTotal.set(0);
        nanoMax.set(0);
        lastErrorMessage = "";
        LastErrorStackTrace = "";
        lastErrorTime = null;
    }

    public CompositeData getCompositeData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("Class", springMethodItemKey.getClassName());
        map.put("Method", springMethodItemKey.getMethod());
        map.put("Count", getInvokCount());
        map.put("ConcurrentMax", getConcurrentMax());
        map.put("RunningCount", getRunningCount());

        map.put("NanoTotal", getNanoTotal());
        map.put("NanoMax", getNanoMax());
        map.put("ErrorCount", getErrorCount());
        map.put("LastErrorMessage", getLastErrorMessage());
        map.put("LastErrorStackTrace", getLastErrorStackTrace());
        map.put("LastErrorTime", getLastErrorTime());

        return new CompositeDataSupport(getCompositeType(), map);
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {
        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }
        OpenType<?>[] indexTypes = new OpenType<?>[] {
                // 1
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //

                // 2
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.STRING, //
                SimpleType.STRING, //

                // 3
                SimpleType.DATE, //
        };
        String[] indexNames = {
                // 1
                "Class", //
                "Method", //
                "Count", //
                "ConcurrentMax", //
                "RunningCount", //

                // 2
                "NanoTotal", //
                "NanoMax", //
                "ErrorCount", //
                "LastErrorMessage", //
                "LastErrorStackTrace", //

                // 3
                "LastErrorTime", //

        };
        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("MethodListStatistic", "Spring Method Statistic", indexNames,
                indexDescriptions, indexTypes);
        return COMPOSITE_TYPE;
    }

    public SpringMethodItemKey getSpringMethodItemKey() {
        return springMethodItemKey;
    }

    public int getRunningCount() {
        return runningCount.get();
    }

    public int getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getInvokCount() {
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

    public String getLastErrorStackTrace() {
        return LastErrorStackTrace;
    }

}
