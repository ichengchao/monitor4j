/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.client.protocal.message;

import cn.jmonitor.monitor4j.common.JmonitorConstants;

/**
 * @author charles 2013年12月23日 下午1:21:51
 */
public class GetAttributeResp extends BaseMessage {

    private Object value;

    public GetAttributeResp(Object value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return JmonitorConstants.MSG_GETATTRIBUTE_RESP;
    }

    @Override
    public Object getBody() {
        return value;
    }

    @Override
    public boolean isRequestFormat() {
        return false;
    }
}
