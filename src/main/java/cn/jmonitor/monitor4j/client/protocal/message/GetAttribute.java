/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.client.protocal.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jmonitor.monitor4j.common.JmonitorConstants;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author charles 2013年12月23日 下午5:04:57
 */
public class GetAttribute extends BaseMessage {

    private int sequence;

    @JSONField(name = "OBJ_NAME")
    private String objectName;

    @JSONField(name = "ATTRS")
    private List<String> attributeNames;

    @JSONField(name = "OPTS")
    private List<String> options;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public String getType() {
        return JmonitorConstants.MSG_GETATTRIBUTE;
    }

    @Override
    public Map<String, Object> getBody() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("OBJ_NAME", objectName);
        map.put("ATTRS", attributeNames);
        map.put("OPTS", options);
        return map;
    }

    @Override
    public boolean isRequestFormat() {
        return true;
    }
}
