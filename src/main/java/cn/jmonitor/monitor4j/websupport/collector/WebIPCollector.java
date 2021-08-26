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
import cn.jmonitor.monitor4j.websupport.items.WebIP;
import cn.jmonitor.monitor4j.websupport.items.WebIPStat;
import cn.jmonitor.monitor4j.websupport.items.WebSingleIPForWeb;

/**
 * @author charles-dell 2014-1-11 上午10:53:53
 */
public class WebIPCollector extends BaseCollector {

    private static ArrayBlockingQueue<WebIP> webIpQueue = new ArrayBlockingQueue<WebIP>(queueSize);

    public static void doCollect() {

        // ip,reset=true
        GetAttribute ipAttribute = new GetAttribute();
        ipAttribute.setAttributeNames(Arrays.asList("JmonitorDataList"));
        ipAttribute.setObjectName(JmonitorConstants.JMX_WEB_IP_NAME);
        ipAttribute.setOptions(Arrays.asList("reset=true"));
        String ipMapStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(ipAttribute));
        List<WebIPStat> webIPStats = JsonUtils.parseArray(ipMapStr, WebIPStat.class);
        WebIP webIP = new WebIP();
        for (WebIPStat webIPStat : webIPStats) {
            webIP.getIPMap().put(webIPStat.getIp(), webIPStat);
        }
        webIP.setTimeStamp(new Date());
        checkQueueSize(webIpQueue);
        webIpQueue.offer(webIP);

    }

    public static String getWebIPForHtml(Integer timeInterval) {
        if (null == timeInterval) {
            throw new IllegalArgumentException("timeInterval can not be null!");
        }

        Map<String, WebIPStat> map = new HashMap<String, WebIPStat>();
        int count = 0;
        String keyTitle = "IP";
        for (WebIP ip : webIpQueue) {
            count++;
            mergeWebIPMap(map, ip.getIPMap());
            if (count == timeInterval) {
                break;
            }
        }
        String result = "<table style='width: 94%;'>";
        result += "<tr><td class='title'>" + keyTitle + "(count:" + map.size() + ")"
                + "</td><td class='titleMin'>访问次数</td><td class='titleMin'>最大并发</td><td class='titleMin'>平均耗时(ms)</td><td class='titleMin'>最大耗时(ms)</td><td class='titleMin'>错误数</td></tr>";
        List<WebIPStat> webIPStatList = new ArrayList<WebIPStat>(map.values());
        Collections.sort(webIPStatList, new JmonitorDataComparator("count", "DESC"));
        for (WebIPStat entry : webIPStatList) {
            result += "<tr>";
            result += "<td><a href='#' onclick='createWebIPDetailChart(\"" + entry.getIp()
                    + "\")'><img src='css/images/chart.png' class='middle-img'></a>" + entry.getIp() + "</td>";
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

    private static void mergeWebIPMap(Map<String, WebIPStat> resultMap, Map<String, WebIPStat> targetMap) {
        if (null == resultMap || null == targetMap) {
            return;
        }
        for (Map.Entry<String, WebIPStat> entry : targetMap.entrySet()) {
            WebIPStat webIPStat = resultMap.get(entry.getKey());
            if (null == webIPStat) {
                webIPStat = new WebIPStat();
                webIPStat.setIp(new String(entry.getKey()));
                resultMap.put(webIPStat.getIp(), webIPStat);
            }
            // 聚合数据,最大值取最大
            webIPStat.setCount(webIPStat.getCount() + entry.getValue().getCount());
            webIPStat.setErrorCount(webIPStat.getErrorCount() + entry.getValue().getErrorCount());
            webIPStat.setNanoTotal(webIPStat.getNanoTotal() + entry.getValue().getNanoTotal());
            if (entry.getValue().getConcurrentMax() > webIPStat.getConcurrentMax()) {
                webIPStat.setConcurrentMax(entry.getValue().getConcurrentMax());
            }
            if (entry.getValue().getNanoMax() > webIPStat.getNanoMax()) {
                webIPStat.setNanoMax(entry.getValue().getNanoMax());
            }
        }
    }

    public static List<WebSingleIPForWeb> getWebSingleIPForWebList(String ip) {
        List<WebSingleIPForWeb> result = new ArrayList<WebSingleIPForWeb>();
        List<WebIP> list = new ArrayList<WebIP>();
        for (WebIP webIP : webIpQueue) {
            list.add(webIP);
        }
        for (WebIP webIP : list) {
            WebIPStat stat = webIP.getIPMap().get(ip);
            if (null != stat) {
                WebSingleIPForWeb ipForWeb = new WebSingleIPForWeb(ip, stat, webIP.getTimeStamp());
                result.add(ipForWeb);
            }
        }
        return result;
    }

    public static String getWebIPErrorForHtml(String ip) {
        Map<String, String> map = new HashMap<String, String>();
        int count = 0;
        for (WebIP webIP : webIpQueue) {
            WebIPStat stat = webIP.getIPMap().get(ip);
            if (null == stat) {
                continue;
            }
            if (StringUtils.isBlank(stat.getLastErrorMsg())) {
                continue;
            }
            map.put(DateFormat.format(webIP.getTimeStamp()), stat.getLastErrorMsg());
            count++;
            if (count == 10) {
                break;
            }
        }
        String result = "<table style='width: 100%;'>";
        result += "<tr><td class='titleMin'>时间</td><td class='titleMin'>错误信息(" + ip + ")</td></tr>";
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
