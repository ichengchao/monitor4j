/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web;

import cn.jmonitor.monitor4j.plugin.ip.WebIPItem;

/**
 * @author charles 2014年1月15日 下午3:49:23
 */
public class WebIPEntryLocal {

    private String ip;
    private long startTime;

    private boolean hasError = false;
    private String errorMsg;

    private WebIPItem webIPItem;

    public WebIPEntryLocal(String ip, long startTime) {
        super();
        this.ip = ip;
        this.startTime = startTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public WebIPItem getWebIPItem() {
        return webIPItem;
    }

    public void setWebIPItem(WebIPItem webIPItem) {
        this.webIPItem = webIPItem;
    }

}
