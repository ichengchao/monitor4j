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
public class WebUrl {

    private Map<String, WebUrlStat> uriMap = new ConcurrentHashMap<String, WebUrlStat>();
    private Date timeStamp;

    public Map<String, WebUrlStat> getUriMap() {
        return uriMap;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
