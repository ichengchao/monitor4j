/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web;

import cn.jmonitor.monitor4j.common.JmonitorConstants;
import cn.jmonitor.monitor4j.plugin.ip.WebIPDataManager;
import cn.jmonitor.monitor4j.plugin.ip.WebIPItem;
import cn.jmonitor.monitor4j.utils.StringUtils;

/**
 * @author charles 2014年1月15日 下午2:55:31
 */
public class WebIPDataCalHelper {

    private final static WebIPDataManager webIPDataManager = WebIPDataManager.getInstance();

    private static int maxCount = 2000;

    public static ThreadLocal<WebIPEntryLocal> currentEntry = new ThreadLocal<WebIPEntryLocal>();

    public static boolean isFull() {
        return webIPDataManager.getWebIPMap().size() > maxCount;
    }

    public static void handleBefore(String ip) {
        // 默认不启用ip的采集,需要特殊配置
        if (!JmonitorConstants.enableMonitorIp) {
            return;
        }
        if (StringUtils.isBlank(ip)) {
            return;
        }
        if (webIPDataManager.getWebIPMap().size() > maxCount) {
            return;
        }
        WebIPItem webIPItem = webIPDataManager.getWebIPMap().get(ip);
        if (null == webIPItem) {
            webIPItem = new WebIPItem(ip);
            webIPDataManager.getWebIPMap().put(ip, webIPItem);
        }
        webIPItem.incrementRunningCount();
        WebIPEntryLocal entryLocal = new WebIPEntryLocal(ip, System.nanoTime());
        entryLocal.setWebIPItem(webIPItem);
        currentEntry.set(entryLocal);
    }

    public static void handleException(boolean hasError, String errorMsg) {
        if (!JmonitorConstants.enableMonitorIp) {
            return;
        }
        WebIPEntryLocal webIPEntryLocal = currentEntry.get();
        if (null == webIPEntryLocal) {
            return;
        }
        webIPEntryLocal.setHasError(hasError);
        webIPEntryLocal.setErrorMsg(errorMsg);
    }

    public static void handleAfter() {
        if (!JmonitorConstants.enableMonitorIp) {
            return;
        }
        WebIPEntryLocal webIPEntryLocal = WebIPDataCalHelper.currentEntry.get();
        if (null == webIPEntryLocal) {
            return;
        }
        WebIPItem webIPItem = webIPEntryLocal.getWebIPItem();
        long startNano = webIPEntryLocal.getStartTime();
        boolean error = webIPEntryLocal.isHasError();
        String errorMsg = webIPEntryLocal.getErrorMsg();
        if (webIPItem != null) {
            webIPItem.handleAfter(startNano, error, errorMsg);
        }
        currentEntry.set(null);
    }

    public static void setMaxCount(int maxCount) {
        WebIPDataCalHelper.maxCount = maxCount;
    }

}
