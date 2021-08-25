/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.log4j;

import javax.management.JMException;
import javax.management.openmbean.TabularData;

/**
 * @author charles 2013年12月20日 下午3:33:53
 */
public interface Log4jDataManagerMBean {

    void reset();

    long getResetCount();

    String getLogConfig();

    TabularData getJmonitorDataList() throws JMException;

}
