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
import cn.jmonitor.monitor4j.websupport.JmonitorDataComparator;
import cn.jmonitor.monitor4j.websupport.items.WebSingleUrlForWeb;
import cn.jmonitor.monitor4j.websupport.items.WebUrl;
import cn.jmonitor.monitor4j.websupport.items.WebUrlStat;

/**
 * @author charles-dell 2014-1-11 上午10:53:53
 */
public class WebUrlCollector extends BaseCollector {

    private static ArrayBlockingQueue<WebUrl> urlQueue = new ArrayBlockingQueue<WebUrl>(queueSize);

    public static void doCollect() {

        // url,reset=true
        GetAttribute urlAttribute = new GetAttribute();
        urlAttribute.setAttributeNames(Arrays.asList("JmonitorDataList"));
        urlAttribute.setObjectName(JmonitorConstants.JMX_WEB_URL_NAME);
        urlAttribute.setOptions(Arrays.asList("reset=true"));
        String urlMapStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(urlAttribute));
        List<WebUrlStat> webUrlStats = JsonUtils.parseArray(urlMapStr, WebUrlStat.class);
        WebUrl webUrl = new WebUrl();
        for (WebUrlStat webUrlStat : webUrlStats) {
            webUrl.getUriMap().put(webUrlStat.getUrl(), webUrlStat);
        }
        webUrl.setTimeStamp(new Date());
        checkQueueSize(urlQueue);
        urlQueue.offer(webUrl);

    }

    public static String getWebUrlForHtml(Integer timeInterval) {
        if (null == timeInterval) {
            throw new IllegalArgumentException("timeInterval can not be null!");
        }

        Map<String, WebUrlStat> map = new HashMap<String, WebUrlStat>();
        int count = 0;
        String keyTitle = "URL";
        for (WebUrl webUrl : urlQueue) {
            count++;
            mergeWebUrlMap(map, webUrl.getUriMap());
            if (count == timeInterval) {
                break;
            }
        }
        String result = "<table style='width: 94%;'>";
        result += "<tr><td class='title'>" + keyTitle + "(count:" + map.size() + ")"
                + "</td><td class='titleMin'>访问次数</td><td class='titleMin'>最大并发</td><td class='titleMin'>平均耗时(ms)</td><td class='titleMin'>最大耗时(ms)</td><td class='titleMin'>错误数</td></tr>";
        List<WebUrlStat> webUrlStatList = new ArrayList<WebUrlStat>(map.values());
        Collections.sort(webUrlStatList, new JmonitorDataComparator("count", "DESC"));
        for (WebUrlStat entry : webUrlStatList) {
            long weburlCount = entry.getCount();
            if (weburlCount == 0) {
                weburlCount = 1;
            }
            result += "<tr>";
            result += "<td><a href='javascript:;' onclick='createWebUrlDetailChart(\"" + entry.getUrl()
                    + "\")'><img src='css/images/chart.png' class='middle-img'></a><a href='javascript:;' onclick='showUrlProfile(\""
                    + entry.getUrl() + "\")'><img src='css/images/more.png' class='middle-img'></a>" + entry.getUrl()
                    + "</td>";
            result += "<td>" + entry.getCount() + "</td>";
            result += "<td>" + entry.getConcurrentMax() + "</td>";
            result += "<td>" + entry.getNanoTotal() / (weburlCount * 1000000) + "</td>";
            result += "<td>" + entry.getNanoMax() / 1000000 + "</td>";
            result += "<td>" + entry.getErrorCount() + "</td>";
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }

    private static void mergeWebUrlMap(Map<String, WebUrlStat> resultMap, Map<String, WebUrlStat> targetMap) {
        if (null == resultMap || null == targetMap) {
            return;
        }
        for (Map.Entry<String, WebUrlStat> entry : targetMap.entrySet()) {
            WebUrlStat webUrlStat = resultMap.get(entry.getKey());
            if (null == webUrlStat) {
                webUrlStat = new WebUrlStat();
                webUrlStat.setUrl(new String(entry.getKey()));
                resultMap.put(webUrlStat.getUrl(), webUrlStat);
            }
            // 聚合数据,最大值取最大
            webUrlStat.setCount(webUrlStat.getCount() + entry.getValue().getCount());
            webUrlStat.setErrorCount(webUrlStat.getErrorCount() + entry.getValue().getErrorCount());
            webUrlStat.setNanoTotal(webUrlStat.getNanoTotal() + entry.getValue().getNanoTotal());
            if (entry.getValue().getConcurrentMax() > webUrlStat.getConcurrentMax()) {
                webUrlStat.setConcurrentMax(entry.getValue().getConcurrentMax());
            }
            if (entry.getValue().getNanoMax() > webUrlStat.getNanoMax()) {
                webUrlStat.setNanoMax(entry.getValue().getNanoMax());
            }
        }
    }

    public static List<WebSingleUrlForWeb> getWebSingleUrlForWebList(String url) {
        List<WebSingleUrlForWeb> result = new ArrayList<WebSingleUrlForWeb>();
        List<WebUrl> list = new ArrayList<WebUrl>();
        for (WebUrl webUrl : urlQueue) {
            list.add(webUrl);
        }
        for (WebUrl webUrl : list) {
            WebUrlStat stat = webUrl.getUriMap().get(url);
            if (null != stat) {
                WebSingleUrlForWeb urlForWeb = new WebSingleUrlForWeb(url, stat, webUrl.getTimeStamp());
                result.add(urlForWeb);
            }
        }
        return result;
    }

    public static String getWebUrlErrorForHtml(String url) {
        Map<String, String> map = new HashMap<String, String>();
        int count = 0;
        for (WebUrl webUrl : urlQueue) {
            WebUrlStat webUrlStat = webUrl.getUriMap().get(url);
            if (null == webUrlStat) {
                continue;
            }
            if (null == webUrlStat.getLastErrorMsg() || "".equals(webUrlStat.getLastErrorMsg())) {
                continue;
            }
            map.put(DateFormat.format(webUrl.getTimeStamp()), webUrlStat.getLastErrorMsg());
            count++;
            if (count == 10) {
                break;
            }
        }
        String result = "<table style='width: 100%;'>";
        result += "<tr><td class='titleMin'>时间</td><td class='titleMin'>错误信息(" + url + ")</td></tr>";
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
