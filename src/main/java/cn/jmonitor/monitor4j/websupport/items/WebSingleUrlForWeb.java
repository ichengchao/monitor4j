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
public class WebSingleUrlForWeb {

    String url;
    WebUrlStat webUrlStat;
    private Date timeStamp;

    public WebSingleUrlForWeb(String url, WebUrlStat webUrlStat, Date timeStamp) {
        super();
        this.url = url;
        this.webUrlStat = webUrlStat;
        this.timeStamp = timeStamp;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the webUrlStat
     */
    public WebUrlStat getWebUrlStat() {
        return webUrlStat;
    }

    /**
     * @param webUrlStat the webUrlStat to set
     */
    public void setWebUrlStat(WebUrlStat webUrlStat) {
        this.webUrlStat = webUrlStat;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
