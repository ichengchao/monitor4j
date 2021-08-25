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
import cn.jmonitor.monitor4j.websupport.items.SpringMethod;
import cn.jmonitor.monitor4j.websupport.items.SpringMethodInfo;
import cn.jmonitor.monitor4j.websupport.items.SpringMethodSingleForWeb;
import cn.jmonitor.monitor4j.websupport.items.SpringMethodStat;

/**
 * @author charles-dell 2014-1-11 上午10:53:53
 */
public class SpringMethodCollector extends BaseCollector {

    private static ArrayBlockingQueue<SpringMethod> springMethodQueue = new ArrayBlockingQueue<SpringMethod>(queueSize);

    public static void doCollect() {

        // spring method,reset=true
        GetAttribute springAttribute = new GetAttribute();
        springAttribute.setAttributeNames(Arrays.asList("JmonitorDataList"));
        springAttribute.setObjectName(JmonitorConstants.JMX_SPRING_METHOD_NAME);
        springAttribute.setOptions(Arrays.asList("reset=true"));
        String springMapStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(springAttribute));
        List<SpringMethodStat> springMethodStats = JsonUtils.parseArray(springMapStr, SpringMethodStat.class);
        SpringMethod springMethod = new SpringMethod();
        for (SpringMethodStat springMethodStat : springMethodStats) {
            springMethod.getMethodMap().put(springMethodStat.buildSpringMethodInfo(), springMethodStat);
        }
        springMethod.setTimeStamp(new Date());
        checkQueueSize(springMethodQueue);
        springMethodQueue.offer(springMethod);

    }

    public static String getSpringMethodForHtml(Integer timeInterval) {
        if (null == timeInterval) {
            throw new IllegalArgumentException("timeInterval can not be null!");
        }

        Map<SpringMethodInfo, SpringMethodStat> map = new HashMap<SpringMethodInfo, SpringMethodStat>();
        int count = 0;
        ArrayBlockingQueue<SpringMethod> queue = springMethodQueue;
        for (SpringMethod springMethod : queue) {
            count++;
            mergeSpringMethodMap(map, springMethod.getMethodMap());
            if (count == timeInterval) {
                break;
            }
        }
        String result = "<table style='width: 94%;'>";
        result += "<tr><td class='title'>类名(count:" + map.size() + ")"
                + "</td><td class='title'>方法名称</td><td class='titleMin'>访问次数</td><td class='titleMin'>最大并发</td><td class='titleMin'>平均耗时(ms)</td><td class='titleMin'>最大耗时(ms)</td><td class='titleMin'>错误数</td></tr>";
        List<SpringMethodStat> springMethodStatList = new ArrayList<SpringMethodStat>(map.values());
        Collections.sort(springMethodStatList, new JmonitorDataComparator("count", "DESC"));
        for (SpringMethodStat entry : springMethodStatList) {
            result += "<tr>";
            result += "<td><a href='#' onclick='createSpringMethodDetailChart(\"" + entry.getClassName() + "\",\""
                    + entry.getMethod() + "\")'><img src='css/images/chart.png' class='middle-img'></a>"
                    + entry.getClassName() + "</td>";
            result += "<td>" + entry.getMethod() + "</td>";
            result += "<td>" + entry.getCount() + "</td>";
            result += "<td>" + entry.getConcurrentMax() + "</td>";
            result += "<td>" + entry.getNanoTotal() / (entry.getCount() * 1000000) + "</td>";
            result += "<td>" + entry.getNanoMax() / 1000000 + "</td>";
            result += "<td>" + entry.getErrorCount() + "</td>";
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }

    private static void mergeSpringMethodMap(Map<SpringMethodInfo, SpringMethodStat> resultMap,
            Map<SpringMethodInfo, SpringMethodStat> targetMap) {
        if (null == resultMap || null == targetMap) {
            return;
        }
        for (Map.Entry<SpringMethodInfo, SpringMethodStat> entry : targetMap.entrySet()) {
            SpringMethodStat springMethodStat = resultMap.get(entry.getKey());
            if (null == springMethodStat) {
                springMethodStat = new SpringMethodStat();
                springMethodStat.setClassName(entry.getKey().getClassName());
                springMethodStat.setMethod(entry.getKey().getMethod());
                resultMap.put(entry.getKey(), springMethodStat);
            }
            // 聚合数据,最大值取最大
            springMethodStat.setCount(springMethodStat.getCount() + entry.getValue().getCount());
            springMethodStat.setErrorCount(springMethodStat.getErrorCount() + entry.getValue().getErrorCount());
            springMethodStat.setNanoTotal(springMethodStat.getNanoTotal() + entry.getValue().getNanoTotal());
            if (entry.getValue().getConcurrentMax() > springMethodStat.getConcurrentMax()) {
                springMethodStat.setConcurrentMax(entry.getValue().getConcurrentMax());
            }
            if (entry.getValue().getNanoMax() > springMethodStat.getNanoMax()) {
                springMethodStat.setNanoMax(entry.getValue().getNanoMax());
            }
        }
    }

    public static List<SpringMethodSingleForWeb> getSingleSpringMethodForWebList(String className, String method) {
        List<SpringMethodSingleForWeb> result = new ArrayList<SpringMethodSingleForWeb>();
        SpringMethodInfo springMethodInfo = new SpringMethodInfo(className, method);
        for (SpringMethod springMethod : springMethodQueue) {
            SpringMethodStat stat = springMethod.getMethodMap().get(springMethodInfo);
            if (null != stat) {
                SpringMethodSingleForWeb methodSingleForWeb = new SpringMethodSingleForWeb(springMethodInfo, stat,
                        springMethod.getTimeStamp());
                result.add(methodSingleForWeb);
            }
        }
        return result;
    }

    public static String getSpringMethodErrorForHtml(String className, String method) {
        Map<String, String> map = new HashMap<String, String>();
        SpringMethodInfo methodInfo = new SpringMethodInfo(className, method);
        int count = 0;
        for (SpringMethod springMethod : springMethodQueue) {
            SpringMethodStat springMethodStat = springMethod.getMethodMap().get(methodInfo);
            if (null == springMethodStat) {
                continue;
            }
            if (StringUtils.isBlank(springMethodStat.getLastErrorMsg())) {
                continue;
            }
            map.put(DateFormat.format(springMethod.getTimeStamp()), springMethodStat.getLastErrorMsg());
            count++;
            if (count == 10) {
                break;
            }
        }
        String result = "<table style='width: 100%;'>";
        result += "<tr><td class='titleMin'>时间</td><td class='titleMin'>错误信息(" + method + ")</td></tr>";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result += "<tr>";
            result += "<td>" + entry.getKey() + "</td>";
            result += "<td>" + entry.getValue() + "</td>";
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }
}
