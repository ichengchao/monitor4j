/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;

/**
 * @author charles 2013年11月27日 下午6:59:46
 */
public class WebSingleIPForWeb {

    String ip;
    WebIPStat webIPStat;
    private Date timeStamp;

    /**
     * @param ip
     * @param webIPStat
     * @param timeStamp
     */
    public WebSingleIPForWeb(String ip, WebIPStat webIPStat, Date timeStamp) {
        super();
        this.ip = ip;
        this.webIPStat = webIPStat;
        this.timeStamp = timeStamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public WebIPStat getWebIPStat() {
        return webIPStat;
    }

    public void setWebIPStat(WebIPStat webIPStat) {
        this.webIPStat = webIPStat;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
