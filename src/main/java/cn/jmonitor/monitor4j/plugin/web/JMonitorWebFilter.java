/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import cn.jmonitor.monitor4j.common.JmonitorConstants;
import cn.jmonitor.monitor4j.utils.FileUtils;
import cn.jmonitor.monitor4j.utils.WebUtils;

/**
 * @author charles 2013年10月28日 下午1:32:31
 */
public class JMonitorWebFilter implements Filter {

    private final List<String> exclusions = new ArrayList<String>();

    static {
        FileUtils.appendToLog("cn.jmonitor.monitor4j.plugin.web.JMonitorWebFilter started.");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 设置忽略的url后缀列表
        String paramExclusions = filterConfig.getInitParameter("exclusions");
        if (paramExclusions != null) {
            String[] items = paramExclusions.split(",");
            for (String item : items) {
                if (item != null && item.length() != 0) {
                    exclusions.add(item);
                }
            }
        }

        // 设置最大采集条数
        String maxCountStr = filterConfig.getInitParameter("maxCount");
        if (null != maxCountStr) {
            int maxCount = Integer.valueOf(maxCountStr);
            if (maxCount > 0) {
                WebUrlDataCalHelper.setMaxCount(maxCount);
                WebIPDataCalHelper.setMaxCount(maxCount);
            }
        }

    }

    // 抽象出来能让使用方重写该方法
    public String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 防止对应用有影响
        if (WebUrlDataCalHelper.isFull()) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = getRequestURI(httpServletRequest);
        // exclude url suffix
        if (isExclusion(url)) {
            chain.doFilter(request, response);
            return;
        }
        WebUrlDataCalHelper.handleBefore(url);

        if (JmonitorConstants.enableMonitorIp) {
            String ip = WebUtils.getClientAddress(httpServletRequest);
            WebIPDataCalHelper.handleBefore(ip);
        }
        boolean hasError = false;
        String errorMsg = null;

        try {
            chain.doFilter(httpServletRequest, response);
        } catch (ServletException ex) {
            hasError = true;
            errorMsg = ex.getMessage();
            throw ex;
        } catch (IOException ex) {
            hasError = true;
            errorMsg = ex.getMessage();
            throw ex;
        } catch (RuntimeException ex) {
            hasError = true;
            errorMsg = ex.getMessage();
            throw ex;
        } catch (Error ex) {
            hasError = true;
            errorMsg = ex.getMessage();
            throw ex;
        } finally {
            WebUrlDataCalHelper.handleException(hasError, errorMsg);
            WebUrlDataCalHelper.handleAfter();
            WebIPDataCalHelper.handleException(hasError, errorMsg);
            WebIPDataCalHelper.handleAfter();
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isExclusion(String uri) {
        for (String exclusion : this.exclusions) {
            if (uri.endsWith(exclusion)) {
                return true;
            }
        }
        return false;
    }
}
