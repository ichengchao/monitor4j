package cn.jmonitor.monitor4j.websupport.collector;

import java.util.Arrays;
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
import cn.jmonitor.monitor4j.websupport.items.WebProfile;
import cn.jmonitor.monitor4j.websupport.items.WebProfileInfo;
import cn.jmonitor.monitor4j.websupport.items.WebProfileStat;

public class WebProfileCollector extends BaseCollector {

    private static ArrayBlockingQueue<WebProfile> queue = new ArrayBlockingQueue<WebProfile>(queueSize);

    public static void doCollect() {
        // spring method,reset=true
        GetAttribute getAttribute = new GetAttribute();
        getAttribute.setAttributeNames(Arrays.asList("JmonitorDataList"));
        getAttribute.setObjectName(JmonitorConstants.JMX_WEB_URL_PROFILE_NAME);
        getAttribute.setOptions(Arrays.asList("reset=true"));
        String mapStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(getAttribute));
        List<WebProfileStat> webProfileStats = JsonUtils.parseArray(mapStr, WebProfileStat.class);
        WebProfile webProfile = new WebProfile();
        for (WebProfileStat webProfileStat : webProfileStats) {
            webProfile.getMap().put(webProfileStat.buildWebProfileInfo(), webProfileStat);
        }
        webProfile.setTimeStamp(new Date());
        checkQueueSize(queue);
        queue.offer(webProfile);
    }

    public static String getWebProfileForHtml(String url, Integer timeInterval) {
        if (null == timeInterval) {
            throw new IllegalArgumentException("timeInterval can not be null!");
        }
        if (null == url) {
            throw new IllegalArgumentException("url can not be null!");
        }

        Map<WebProfileInfo, WebProfileStat> map = new HashMap<WebProfileInfo, WebProfileStat>();
        int count = 0;
        for (WebProfile webProfile : queue) {
            count++;
            mergeMap(map, webProfile.getMap());
            if (count == timeInterval) {
                break;
            }
        }
        String result = "<table style='width: 100%;'>";
        result += "<tr><td class='title'>类型(count:" + map.size() + ")"
                + "</td><td class='title'>名称</td><td class='titleMin'>访问次数</td><td class='titleMin'>最大并发</td><td class='titleMin'>平均耗时(ms)</td><td class='titleMin'>最大耗时(ms)</td><td class='titleMin'>错误数</td></tr>";
        for (WebProfileStat entry : map.values()) {
            if (!url.equals(entry.getUrl())) {
                continue;
            }
            result += "<tr>";
            result += "<td>" + entry.getType() + "</td>";
            result += "<td>" + entry.getName() + "</td>";
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

    private static void mergeMap(Map<WebProfileInfo, WebProfileStat> resultMap,
            Map<WebProfileInfo, WebProfileStat> targetMap) {
        if (null == resultMap || null == targetMap) {
            return;
        }
        for (Map.Entry<WebProfileInfo, WebProfileStat> entry : targetMap.entrySet()) {
            WebProfileStat webProfileStat = resultMap.get(entry.getKey());
            if (null == webProfileStat) {
                webProfileStat = new WebProfileStat();
                webProfileStat.setUrl(entry.getKey().getUrl());
                webProfileStat.setType(entry.getKey().getType());
                webProfileStat.setName(entry.getKey().getName());
                resultMap.put(entry.getKey(), webProfileStat);
            }
            // 聚合数据,最大值取最大
            webProfileStat.setCount(webProfileStat.getCount() + entry.getValue().getCount());
            webProfileStat.setErrorCount(webProfileStat.getErrorCount() + entry.getValue().getErrorCount());
            webProfileStat.setNanoTotal(webProfileStat.getNanoTotal() + entry.getValue().getNanoTotal());
            if (entry.getValue().getConcurrentMax() > webProfileStat.getConcurrentMax()) {
                webProfileStat.setConcurrentMax(entry.getValue().getConcurrentMax());
            }
            if (entry.getValue().getNanoMax() > webProfileStat.getNanoMax()) {
                webProfileStat.setNanoMax(entry.getValue().getNanoMax());
            }
            // merge lastErrorMsg&lastErrorTime
            if (StringUtils.isNotBlank(entry.getValue().getLastErrorMsg())
                    && entry.getValue().getLastErrorTime() != null) {
                if (webProfileStat.getLastErrorTime() == null || webProfileStat.getLastErrorTime().getTime() < entry
                        .getValue().getLastErrorTime().getTime()) {
                    webProfileStat.setLastErrorTime(entry.getValue().getLastErrorTime());
                    webProfileStat.setLastErrorMsg(entry.getValue().getLastErrorMsg());
                }
            }
        }
    }

}
