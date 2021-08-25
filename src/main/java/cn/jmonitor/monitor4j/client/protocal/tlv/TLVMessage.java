/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.client.protocal.tlv;

/**
 * 类TLVMessage.java的实现描述：tlv协议格式(type|length|value)
 * 
 * @author charles 2012-7-31 上午10:39:00
 */
public class TLVMessage {

    // 类型:json,xml,binary等
    private short type;
    // value的长度
    private int length;

    private byte[] value;

    public TLVMessage() {
    }

    public TLVMessage(short type, int length, byte[] value) {
        super();
        this.type = type;
        this.length = length;
        this.value = value;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

}
