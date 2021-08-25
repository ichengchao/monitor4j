/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.client.protocal.message;

import java.util.HashMap;
import java.util.Map;

import cn.jmonitor.monitor4j.common.JmonitorConstants;

/**
 * @author charles 2013年12月25日 下午3:25:37
 */
public class Connect extends BaseMessage {

    private String appNum;

    public Connect(String appNum) {
        this.appNum = appNum;
    }

    @Override
    public String getType() {
        return JmonitorConstants.MSG_CONNECT;
    }

    @Override
    public Map<String, Object> getBody() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("VERSION", JmonitorConstants.VERSION);
        map.put("APP_NUM", appNum);
        // 兼容dragoon协议,instNum和pid暂时都不支持
        map.put("INST_NUM", null);
        map.put("PID", null);
        return map;
    }

    @Override
    public boolean isRequestFormat() {
        return true;
    }

}
