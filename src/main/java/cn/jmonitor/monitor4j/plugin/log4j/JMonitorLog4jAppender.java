/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.log4j;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import cn.jmonitor.monitor4j.utils.FileUtils;

/**
 * @author charles 2013年12月4日 上午11:00:38
 */
public class JMonitorLog4jAppender extends AppenderSkeleton {

    private final Log4jDataManager log4jDataManager = Log4jDataManager.getInstance();

    // 是否记录没有堆栈的异常
    private boolean enableNoStackTrace = true;

    // 每个时间段采集的最大的数量,超过这个数量将抛弃
    private int maxCount = 2000;

    private int maxStackTraceLength = 2000;

    static {
        FileUtils.appendToLog("cn.jmonitor.monitor4j.plugin.log4j.JMonitorLog4jAppender started.");
    }

    @Override
    protected void append(LoggingEvent event) {
        try {
            // 暴露配置到mbean中,方便排查问题
            if (null == log4jDataManager.getLogConfig()) {
                log4jDataManager.setLogConfig("enableNoStackTrace:" + enableNoStackTrace + " ,maxCount:" + maxCount);
            }
            // 只记录error级别的日志
            if (!Level.ERROR.equals(event.getLevel())) {
                return;
            }

            // 防止影响应用
            if (log4jDataManager.getLogMap().size() > maxCount) {
                return;
            }

            ThrowableInformation errorInfo = event.getThrowableInformation();
            if (null == errorInfo) {
                if (!enableNoStackTrace) {
                    return;
                }
            }

            LocationInfo locationInfo = event.getLocationInformation();
            if (null == locationInfo) {
                return;
            }

            String exceptionType = "None";
            String stackTrace = "None";
            if (null != errorInfo) {
                exceptionType = errorInfo.getThrowable().getClass().getName();
                Throwable throwable = errorInfo.getThrowable();
                if (throwable != null) {
                    // for (StackTraceElement element : throwable.getStackTrace()) {
                    // stackTrace.append(element.getClassName() + "." + element.getMethodName());
                    // stackTrace.append("(" + element.getFileName() + ":" + element.getLineNumber()
                    // + ")<br>");
                    // }
                    StringWriter buf = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(buf));
                    stackTrace = buf.toString();
                    // 截断消息,防止数据过长
                    if (stackTrace.length() > maxStackTraceLength) {
                        stackTrace.substring(0, maxStackTraceLength);
                    }
                }
            }
            String methodName = getMethodName(event);
            // // 如果拿不到locationInfo的信息,methodName就取loggerName
            // if (LocationInfo.NA.equals(locationInfo.getClassName())) {
            // methodName = event.getLoggerName();
            // } else {
            // methodName = locationInfo.getClassName() + "." +
            // locationInfo.getMethodName();
            // }
            // // String lineNumber = locationInfo.getLineNumber();

            LogItemKey logInfoKey = new LogItemKey(exceptionType, methodName);
            LogItemValue logInfoStat = log4jDataManager.getLogMap().get(logInfoKey);
            if (null == logInfoStat) {
                logInfoStat = new LogItemValue(logInfoKey);
                log4jDataManager.getLogMap().put(logInfoKey, logInfoStat);
            }
            logInfoStat.incrementCount();
            logInfoStat.setLastMessage(String.valueOf(event.getMessage()));
            logInfoStat.setLastStackTrace(stackTrace);
        } catch (Throwable e) {
            LogLog.error("monitorAppender error:" + e.getMessage(), e);
        }
    }

    public String getMethodName(LoggingEvent event) {
        String methodName = null;
        LocationInfo locationInfo = event.getLocationInformation();
        // 如果拿不到locationInfo的信息,methodName就取loggerName
        if (LocationInfo.NA.equals(locationInfo.getClassName())) {
            methodName = event.getLoggerName();
        } else {
            methodName = locationInfo.getClassName() + "." + locationInfo.getMethodName();
        }
        // String lineNumber = locationInfo.getLineNumber();
        return methodName;
    }

    @Override
    public void close() {
        super.closed = true;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public void setEnableNoStackTrace(boolean enableNoStackTrace) {
        this.enableNoStackTrace = enableNoStackTrace;
    }

    public void setMaxStackTraceLength(int maxStackTraceLength) {
        if (maxStackTraceLength < 0) {
            return;
        }
        if (maxStackTraceLength > 10000) {
            return;
        }
        this.maxStackTraceLength = maxStackTraceLength;
    }

    public void setMaxCount(int maxCount) {
        if (maxCount <= 0) {
            return;
        }
        this.maxCount = maxCount;
    }

}
