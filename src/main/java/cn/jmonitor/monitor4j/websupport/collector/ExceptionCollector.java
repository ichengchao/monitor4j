package cn.jmonitor.monitor4j.websupport.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import cn.jmonitor.monitor4j.client.protocal.message.GetAttribute;
import cn.jmonitor.monitor4j.common.JmonitorConstants;
import cn.jmonitor.monitor4j.jmx.JMXUtils;
import cn.jmonitor.monitor4j.utils.JsonUtils;
import cn.jmonitor.monitor4j.utils.StringUtils;
import cn.jmonitor.monitor4j.websupport.JmonitorDataComparator;
import cn.jmonitor.monitor4j.websupport.items.LogInfo;
import cn.jmonitor.monitor4j.websupport.items.LogInfoKey;
import cn.jmonitor.monitor4j.websupport.items.LogInfoSingleInfoForWeb;
import cn.jmonitor.monitor4j.websupport.items.LogInfoStat;

/**
 * @author charles-dell 2014-1-11 上午10:53:53
 */
public class ExceptionCollector extends BaseCollector {

    private static ArrayBlockingQueue<LogInfo> logInfoQueue = new ArrayBlockingQueue<LogInfo>(queueSize);

    private static Map<Long, LogInfo> logInfoMap = new HashMap<Long, LogInfo>();

    public static void doCollect() {

        // exception,reset=true
        GetAttribute exceptionAttribute = new GetAttribute();
        exceptionAttribute.setAttributeNames(Arrays.asList("JmonitorDataList"));
        exceptionAttribute.setObjectName(JmonitorConstants.JMX_EXCEPTION_NAME);
        exceptionAttribute.setOptions(Arrays.asList("reset=true"));
        String exceptionMapStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(exceptionAttribute));
        List<LogInfoStat> logInfoStats = JsonUtils.parseArray(exceptionMapStr, LogInfoStat.class);
        LogInfo loginfo = new LogInfo();
        for (LogInfoStat logInfoStat : logInfoStats) {
            loginfo.getLogMap().put(logInfoStat.buildLogInfoKey(), logInfoStat);
        }
        loginfo.setTimeStamp(new Date());
        logInfoMap.put(loginfo.getTimeStamp().getTime(), loginfo);
        checkQueueSize(logInfoQueue);
        logInfoQueue.offer(loginfo);

    }

    public static String getLogInfoForHtml(Integer timeInterval) {
        if (null == timeInterval) {
            throw new IllegalArgumentException("timeInterval can not be null!");
        }
        Map<LogInfoKey, LogInfoStat> map = new HashMap<LogInfoKey, LogInfoStat>();
        int count = 0;
        for (LogInfo logInfo : logInfoQueue) {
            count++;
            mergeLogInfoMap(map, logInfo.getLogMap());
            if (count == timeInterval) {
                break;
            }
        }
        String result = "<table style='width: 94%;'>";
        result += "<tr><td class='title'>异常类型(count:" + map.size() + ")"
                + "</td><td class='title'>异常方法</td><td class='titleMin'>总数</td></tr>";
        List<LogInfoStat> logInfoStatList = new ArrayList<LogInfoStat>(map.values());
        Collections.sort(logInfoStatList, new JmonitorDataComparator("count", "DESC"));
        for (LogInfoStat entry : logInfoStatList) {
            result += "<tr>";
            result += "<td>" + entry.getType() + "</td>";
            result += "<td>" + entry.getMethod() + "</td>";
            result += "<td><a href='#' onclick='showLogInfoDetail(\"" + entry.getType() + "\",\"" + entry.getMethod()
                    + "\")'>" + entry.getCount() + "</a></td>";
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }

    private static void mergeLogInfoMap(Map<LogInfoKey, LogInfoStat> resultMap,
            Map<LogInfoKey, LogInfoStat> targetMap) {
        if (null == resultMap || null == targetMap) {
            return;
        }
        for (Map.Entry<LogInfoKey, LogInfoStat> entry : targetMap.entrySet()) {
            LogInfoStat logInfoStat = resultMap.get(entry.getKey());
            if (null == logInfoStat) {
                logInfoStat = new LogInfoStat();
                logInfoStat.setType(entry.getKey().getExceptionType());
                logInfoStat.setMethod(entry.getKey().getMethodName());
                resultMap.put(entry.getKey(), logInfoStat);
            }
            logInfoStat.setCount(logInfoStat.getCount() + entry.getValue().getCount());
        }
    }

    public static String getLogInfoStrackTraceForHtml(String exceptionType, String methodName, Long timestamp) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp can not be null!");
        }
        LogInfoKey logInfoKey = new LogInfoKey(exceptionType, methodName);
        LogInfo logInfo = logInfoMap.get(timestamp);
        LogInfoStat logInfoStat = logInfo.getLogMap().get(logInfoKey);
        String lastStackTrace = logInfoStat.getLastStackTrace();
        if (StringUtils.isNotBlank(lastStackTrace)) {
            lastStackTrace = lastStackTrace.replaceAll("\r\n", "<br>");
            lastStackTrace = lastStackTrace.replaceAll(" ", "");
        }
        String result = "<table style='width: 100%;'>";
        result += "<tr><td class='namemin'>异常类型</td><td>" + exceptionType + "</td></tr>";
        result += "<tr><td class='namemin'>异常方法</td><td>" + methodName + "</td></tr>";
        result += "<tr><td class='namemin'>异常数量</td><td>" + logInfoStat.getCount() + "</td></tr>";
        result += "<tr><td class='namemin'>最后时间</td><td>" + DateFormat.format(logInfoStat.getLastDate()) + "</td></tr>";
        result += "<tr><td class='namemin'>最后异常信息</td><td>" + logInfoStat.getLastMessage() + "</td></tr>";
        result += "<tr><td class='namemin'>最后异常堆栈</td><td>" + lastStackTrace + "</td></tr>";
        result += "</table>";
        return result;
    }

    public static List<LogInfoSingleInfoForWeb> getLogInfoSingleInfoForWebList(String exceptionType,
            String methodName) {
        List<LogInfoSingleInfoForWeb> result = new ArrayList<LogInfoSingleInfoForWeb>();
        LogInfoKey logInfoKey = new LogInfoKey(exceptionType, methodName);
        for (LogInfo logInfo : logInfoQueue) {
            LogInfoStat logInfoStat = logInfo.getLogMap().get(logInfoKey);
            if (null != logInfoStat) {
                LogInfoSingleInfoForWeb singleInfoForWeb = new LogInfoSingleInfoForWeb();
                singleInfoForWeb.setCount(logInfoStat.getCount());
                singleInfoForWeb.setTimeStamp(logInfo.getTimeStamp());
                result.add(singleInfoForWeb);
            }
        }
        return result;
    }
}
