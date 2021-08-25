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
import cn.jmonitor.monitor4j.websupport.items.JdbcSql;
import cn.jmonitor.monitor4j.websupport.items.JdbcSqlStat;

/**
 * @author charles-dell 2014-1-11 上午10:53:53
 */
public class SqlCollector extends BaseCollector {

    private static ArrayBlockingQueue<JdbcSql> sqlQueue = new ArrayBlockingQueue<JdbcSql>(queueSize);

    public static void doCollect() {

        // sql,reset=true,如果没有使用druid的话,有可能会报错
        GetAttribute sqlAttribute = new GetAttribute();
        sqlAttribute.setAttributeNames(Arrays.asList("SqlList"));
        sqlAttribute.setObjectName(JmonitorConstants.JMX_SQL_NAME);
        sqlAttribute.setOptions(Arrays.asList("reset=true"));
        String sqlMapStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(sqlAttribute));
        List<JdbcSqlStat> jdbcSqlStats = JsonUtils.parseArray(sqlMapStr, JdbcSqlStat.class);
        JdbcSql jdbcSql = new JdbcSql();
        for (JdbcSqlStat jdbcSqlStat : jdbcSqlStats) {
            jdbcSql.getMap().put(jdbcSqlStat.getSql(), jdbcSqlStat);
        }
        jdbcSql.setTimeStamp(new Date());
        checkQueueSize(sqlQueue);
        sqlQueue.offer(jdbcSql);

    }

    public static String getSqlIForHtml(Integer timeInterval) {
        if (null == timeInterval) {
            throw new IllegalArgumentException("timeInterval can not be null!");
        }

        Map<String, JdbcSqlStat> map = new HashMap<String, JdbcSqlStat>();
        int count = 0;
        String keyTitle = "Sql";
        for (JdbcSql jdbcSql : sqlQueue) {
            count++;
            mergeJdbcSqlMap(map, jdbcSql.getMap());
            if (count == timeInterval) {
                break;
            }
        }
        String result = "<table style='width: 94%;'>";
        result += "<tr><td class='title'>" + keyTitle + "(count:" + map.size() + ")"
                + "</td><td class='titleMin'>访问次数</td><td class='titleMin'>最大并发</td><td class='titleMin'>平均耗时(ms)</td><td class='titleMin'>最大耗时(ms)</td><td class='titleMin'>错误数</td><td class='titleMin'>平均抓取</td><td class='titleMin'>最大抓取</td><td class='titleMin'>平均影响</td><td class='titleMin'>最大影响</td></tr>";
        List<JdbcSqlStat> jdbcSqlStatList = new ArrayList<JdbcSqlStat>(map.values());
        Collections.sort(jdbcSqlStatList, new JmonitorDataComparator("count", "DESC"));
        for (JdbcSqlStat entry : jdbcSqlStatList) {
            result += "<tr>";
            result += "<td><a href='#'></a>"
                    + entry.getSql().replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\t", " ") + "</td>";
            result += "<td>" + entry.getCount() + "</td>";
            result += "<td>" + entry.getConcurrentMax() + "</td>";
            result += "<td>" + entry.getTotalTime() / entry.getCount() + "</td>";
            result += "<td>" + entry.getMaxTime() + "</td>";
            result += "<td>" + entry.getErrorCount() + "</td>";
            result += "<td>" + entry.getFetchRowCount() / entry.getCount() + "</td>";
            result += "<td>" + entry.getFetchRowCountMax() + "</td>";
            result += "<td>" + entry.getEffectedRowCount() / entry.getCount() + "</td>";
            result += "<td>" + entry.getEffectedRowCountMax() + "</td>";
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }

    private static void mergeJdbcSqlMap(Map<String, JdbcSqlStat> resultMap, Map<String, JdbcSqlStat> targetMap) {
        if (null == resultMap || null == targetMap) {
            return;
        }
        for (Map.Entry<String, JdbcSqlStat> entry : targetMap.entrySet()) {
            JdbcSqlStat jdbcSqlStat = resultMap.get(entry.getKey());
            if (null == jdbcSqlStat) {
                jdbcSqlStat = new JdbcSqlStat();
                jdbcSqlStat.setSql(new String(entry.getKey()));
                resultMap.put(jdbcSqlStat.getSql(), jdbcSqlStat);
            }
            // 聚合数据,最大值取最大
            jdbcSqlStat.setCount(jdbcSqlStat.getCount() + entry.getValue().getCount());
            jdbcSqlStat.setErrorCount(jdbcSqlStat.getErrorCount() + entry.getValue().getErrorCount());
            jdbcSqlStat.setTotalTime(jdbcSqlStat.getTotalTime() + entry.getValue().getTotalTime());
            if (entry.getValue().getConcurrentMax() > jdbcSqlStat.getConcurrentMax()) {
                jdbcSqlStat.setConcurrentMax(entry.getValue().getConcurrentMax());
            }
            if (entry.getValue().getMaxTime() > jdbcSqlStat.getMaxTime()) {
                jdbcSqlStat.setMaxTime(entry.getValue().getMaxTime());
            }
            if (entry.getValue().getEffectedRowCountMax() > jdbcSqlStat.getEffectedRowCountMax()) {
                jdbcSqlStat.setEffectedRowCountMax(entry.getValue().getEffectedRowCountMax());
            }
            if (entry.getValue().getFetchRowCountMax() > jdbcSqlStat.getFetchRowCountMax()) {
                jdbcSqlStat.setFetchRowCountMax(entry.getValue().getFetchRowCountMax());
            }
        }
    }
}
