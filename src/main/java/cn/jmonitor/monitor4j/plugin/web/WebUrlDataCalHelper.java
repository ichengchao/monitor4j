/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web;

import cn.jmonitor.monitor4j.utils.StringUtils;

/**
 * @author charles 2014年1月15日 下午2:55:31
 */
public class WebUrlDataCalHelper {

    private final static WebUrlDataManager webUrlDataManager = WebUrlDataManager.getInstance();

    private static int maxCount = 2000;

    public static ThreadLocal<WebEntryLocal> currentWebEntry = new ThreadLocal<WebEntryLocal>();

    public static boolean isFull() {
        return webUrlDataManager.getWebUrlMap().size() > maxCount;
    }

    /**
     * before url request
     * 
     * @param url
     */
    public static void handleBefore(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        if (webUrlDataManager.getWebUrlMap().size() > maxCount) {
            return;
        }
        WebUrlItem webUrlItem = webUrlDataManager.getWebUrlMap().get(url);
        if (null == webUrlItem) {
            webUrlItem = new WebUrlItem(url);
            webUrlDataManager.getWebUrlMap().put(url, webUrlItem);
        }
        webUrlItem.incrementRunningCount();
        WebEntryLocal entryLocal = new WebEntryLocal(url, System.nanoTime());
        entryLocal.setWebUrlItem(webUrlItem);
        currentWebEntry.set(entryLocal);
    }

    public static void handleException(boolean hasError, String errorMsg) {
        WebEntryLocal webEntryLocal = currentWebEntry.get();
        if (null == webEntryLocal) {
            return;
        }
        webEntryLocal.setHasError(hasError);
        webEntryLocal.setErrorMsg(errorMsg);
    }

    /**
     * after url request
     */
    public static void handleAfter() {
        WebEntryLocal webEntryLocal = WebUrlDataCalHelper.currentWebEntry.get();
        if (null == webEntryLocal) {
            return;
        }
        WebUrlItem webUrlItem = webEntryLocal.getWebUrlItem();
        long startNano = webEntryLocal.getStartTime();
        boolean error = webEntryLocal.isHasError();
        String errorMsg = webEntryLocal.getErrorMsg();
        if (webUrlItem != null) {
            webUrlItem.handleAfter(startNano, error, errorMsg);
        }
        currentWebEntry.set(null);
    }

    public static void setMaxCount(int maxCount) {
        WebUrlDataCalHelper.maxCount = maxCount;
    }

}
