/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web;

/**
 * 在url访问的时候放在threadlocal中
 * 
 * @author charles 2014年1月15日 上午10:41:09
 */
public class WebEntryLocal {

    private String url;
    private long startTime;

    private boolean hasError = false;
    private String errorMsg;

    private WebUrlItem webUrlItem;

    public WebEntryLocal(String url, long startTime) {
        super();
        this.url = url;
        this.startTime = startTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public WebUrlItem getWebUrlItem() {
        return webUrlItem;
    }

    public void setWebUrlItem(WebUrlItem webUrlItem) {
        this.webUrlItem = webUrlItem;
    }

}
