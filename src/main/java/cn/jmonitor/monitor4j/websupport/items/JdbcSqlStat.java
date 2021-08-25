/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 单个url的统计信息
 */
public class JdbcSqlStat implements BaseComparable {

    @JSONField(name = "SQL")
    private String sql;

    @JSONField(name = "RunningCount")
    private int runningCount;
    // 最大并发

    @JSONField(name = "ConcurrentMax")
    private int concurrentMax;

    // 总数
    @JSONField(name = "ExecuteCount")
    private long count;

    // 抓取行数
    @JSONField(name = "FetchRowCount")
    private long fetchRowCount;

    // 最大抓取行数
    @JSONField(name = "FetchRowCountMax")
    private long fetchRowCountMax;

    // 影响行数
    @JSONField(name = "EffectedRowCount")
    private long effectedRowCount;

    // 最大影响行数
    @JSONField(name = "EffectedRowCountMax")
    private long effectedRowCountMax;

    @JSONField(name = "ErrorCount")
    private long errorCount;

    // 总耗时
    @JSONField(name = "TotalTime")
    private long totalTime;

    // 最大耗时
    @JSONField(name = "MaxTimespan")
    private long maxTime;

    @JSONField(name = "LastErrorMessage")
    private String lastErrorMsg;

    @JSONField(name = "LastErrorTime")
    private Date lastErrorTime;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getConcurrentMax() {
        return concurrentMax;
    }

    public void setConcurrentMax(int concurrentMax) {
        this.concurrentMax = concurrentMax;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public void setFetchRowCount(long fetchRowCount) {
        this.fetchRowCount = fetchRowCount;
    }

    public long getFetchRowCountMax() {
        return fetchRowCountMax;
    }

    public void setFetchRowCountMax(long fetchRowCountMax) {
        this.fetchRowCountMax = fetchRowCountMax;
    }

    public long getEffectedRowCount() {
        return effectedRowCount;
    }

    public void setEffectedRowCount(long effectedRowCount) {
        this.effectedRowCount = effectedRowCount;
    }

    public long getEffectedRowCountMax() {
        return effectedRowCountMax;
    }

    public void setEffectedRowCountMax(long effectedRowCountMax) {
        this.effectedRowCountMax = effectedRowCountMax;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    public void setLastErrorMsg(String lastErrorMsg) {
        this.lastErrorMsg = lastErrorMsg;
    }

    public Date getLastErrorTime() {
        return lastErrorTime;
    }

    public void setLastErrorTime(Date lastErrorTime) {
        this.lastErrorTime = lastErrorTime;
    }

}
