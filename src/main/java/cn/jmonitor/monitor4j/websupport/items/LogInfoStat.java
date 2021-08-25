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
 * @author charles 2013年12月4日 下午1:57:58
 */
public class LogInfoStat implements BaseComparable {

    @JSONField(name = "Type")
    private String type;

    @JSONField(name = "Method")
    private String method;

    @JSONField(name = "Count")
    private long count;

    @JSONField(name = "LastThrowDate")
    private Date lastDate;

    @JSONField(name = "LastThrowMessage")
    private String lastMessage;

    @JSONField(name = "StackTrace")
    private String lastStackTrace;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastStackTrace() {
        return lastStackTrace;
    }

    public void setLastStackTrace(String lastStackTrace) {
        this.lastStackTrace = lastStackTrace;
    }

    public LogInfoKey buildLogInfoKey() {
        LogInfoKey logInfoKey = new LogInfoKey(type, method);
        return logInfoKey;
    }

}
