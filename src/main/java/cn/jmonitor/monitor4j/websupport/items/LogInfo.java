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
 * @author charles 2013年12月4日 下午1:56:10
 */
public class LogInfo {

    private Map<LogInfoKey, LogInfoStat> logMap = new ConcurrentHashMap<LogInfoKey, LogInfoStat>();
    private Date timeStamp;

    public Map<LogInfoKey, LogInfoStat> getLogMap() {
        return logMap;
    }

    public void setLogMap(Map<LogInfoKey, LogInfoStat> logMap) {
        this.logMap = logMap;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
