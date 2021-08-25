/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.exception;

/**
 * 类AlimonitorJmonitorException.java的实现描述：java采集的异常
 * 
 * @author charles 2013年12月20日 上午11:00:16
 */
public class AlimonitorJmonitorException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AlimonitorJmonitorException(String message) {
        super(message);
    }

    public AlimonitorJmonitorException(String message, Throwable e) {
        super(message, e);
    }

}
