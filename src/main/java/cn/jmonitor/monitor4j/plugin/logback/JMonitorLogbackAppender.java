//package cn.jmonitor.monitor4j.plugin.logback;
//
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import ch.qos.logback.classic.spi.IThrowableProxy;
//import ch.qos.logback.classic.spi.StackTraceElementProxy;
//import ch.qos.logback.core.AppenderBase;
//import cn.jmonitor.monitor4j.plugin.log4j.Log4jDataManager;
//import cn.jmonitor.monitor4j.plugin.log4j.LogItemKey;
//import cn.jmonitor.monitor4j.plugin.log4j.LogItemValue;
//import cn.jmonitor.monitor4j.utils.FileUtils;
//import cn.jmonitor.monitor4j.utils.StringUtils;
//
//public class JMonitorLogbackAppender extends AppenderBase<ILoggingEvent> {
//
//    private final Log4jDataManager log4jDataManager = Log4jDataManager.getInstance();
//
//    // 是否记录没有堆栈的异常
//    // private boolean enableNoStackTrace = true;
//
//    // 每个时间段采集的最大的异常类型数量,超过这个数量将抛弃
//    private int maxCount = 2000;
//
//    private int maxStackTraceLength = 2000;
//
//    static {
//        FileUtils.appendToLog("cn.jmonitor.monitor4j.plugin.logback.JMonitorLogbackAppender started.");
//    }
//
//    @Override
//    protected void append(ILoggingEvent event) {
//        // 只记录error级别的日志
//        if (!Level.ERROR.equals(event.getLevel())) {
//            return;
//        }
//
//        // 防止影响应用
//        if (log4jDataManager.getLogMap().size() > maxCount) {
//            return;
//        }
//
//        String exceptionType = "None";
//        String stackTrace = "None";
//        String methodName = event.getLoggerName();
//        IThrowableProxy iThrowableProxy = event.getThrowableProxy();
//        if (null != iThrowableProxy) {
//            String className = iThrowableProxy.getClassName();
//            if (StringUtils.isNotBlank(className)) {
//                exceptionType = className;
//            }
//            StackTraceElementProxy[] stackTraceArray = iThrowableProxy.getStackTraceElementProxyArray();
//            if (null != stackTraceArray) {
//                StringBuilder sb = new StringBuilder();
//                for (StackTraceElementProxy stackTraceItem : stackTraceArray) {
//                    sb.append(stackTraceItem.toString());
//                    if (sb.length() > maxStackTraceLength) {
//                        break;
//                    }
//                }
//                stackTrace = sb.toString();
//            }
//
//        }
//        LogItemKey logInfoKey = new LogItemKey(exceptionType, methodName);
//
//        LogItemValue logInfoStat = log4jDataManager.getLogMap().get(logInfoKey);
//        if (null == logInfoStat) {
//            logInfoStat = new LogItemValue(logInfoKey);
//            log4jDataManager.getLogMap().put(logInfoKey, logInfoStat);
//        }
//        logInfoStat.incrementCount();
//        logInfoStat.setLastMessage(event.getFormattedMessage());
//        logInfoStat.setLastStackTrace(stackTrace);
//    }
//}
