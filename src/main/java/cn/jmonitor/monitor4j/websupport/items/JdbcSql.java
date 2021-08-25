/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author charles 2013年11月20日 上午9:40:14
 */
public class JdbcSql {

    private Map<String, JdbcSqlStat> map = new ConcurrentHashMap<String, JdbcSqlStat>();
    private Date timeStamp;

    public Map<String, JdbcSqlStat> getMap() {
        return map;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
