//package cn.jmonitor.monitor4j.websupport.collector;
//
//import java.util.Arrays;
//import java.util.List;
//
//import cn.jmonitor.monitor4j.client.protocal.message.GetAttribute;
//import cn.jmonitor.monitor4j.common.JmonitorConstants;
//import cn.jmonitor.monitor4j.jmx.JMXUtils;
//import cn.jmonitor.monitor4j.utils.JsonUtils;
//import cn.jmonitor.monitor4j.websupport.items.JdbcInfoStat;
//
///**
// * @author charles-dell 2014-1-11 上午10:53:53
// */
//public class DruidDataSourceCollector extends BaseCollector {
//
//    public static String getJDBCInfoHtml() {
//        // memory
//        GetAttribute getAttribute = new GetAttribute();
//        getAttribute.setAttributeNames(Arrays.asList("DataSourceList"));
//        getAttribute.setObjectName(JmonitorConstants.JMX_DRUID_DATASOURCE_NAME);
//        String druidDatasourceStr = JsonUtils.toJsonString(JMXUtils.getAttributeFormatted(getAttribute));
//        List<JdbcInfoStat> jdbcInfoStats = JsonUtils.parseArray(druidDatasourceStr, JdbcInfoStat.class);
//        if (null == jdbcInfoStats) {
//            return "can not find druid mbean:  " + JmonitorConstants.JMX_DRUID_DATASOURCE_NAME;
//        }
//        String result = "";
//        for (JdbcInfoStat jdbcInfoStat : jdbcInfoStats) {
//            result += "<table style='width: 100%;'>";
//            result += "<tr><td class='name'>连接信息</td><td>" + jdbcInfoStat.getUrl() + "</td></tr>";
//            result += "<tr><td class='name'>用户名</td><td>" + jdbcInfoStat.getUserName() + "</td></tr>";
//            result += "<tr><td class='name'>数据库类型</td><td>" + jdbcInfoStat.getDbType() + "</td></tr>";
//            result += "<tr><td class='name'>驱动</td><td>" + jdbcInfoStat.getDriverClassName() + "</td></tr>";
//            result += "<tr><td class='name'>错误数</td><td>" + jdbcInfoStat.getErrorCount() + "</td></tr>";
//            result += "<tr><td class='name'>最小连接数</td><td>" + jdbcInfoStat.getMinIdle() + "</td></tr>";
//            result += "<tr><td class='name'>最大连接数</td><td>" + jdbcInfoStat.getMaxActive() + "</td></tr>";
//            result += "<tr><td class='name'>池中连接数</td><td>" + jdbcInfoStat.getPoolingCount() + "</td></tr>";
//            result += "<tr><td class='name'>名称</td><td>" + jdbcInfoStat.getName() + "</td></tr>";
//            result += "</table>";
//            result += "<br><hr>";
//        }
//
//        return result;
//    }
//
//}
