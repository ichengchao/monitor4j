/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web.profile;

import javax.management.JMException;
import javax.management.openmbean.TabularData;

/**
 * @author charles 2013年12月30日 下午1:35:51
 */
public interface WebUrlProfileDataManagerMBean {

    TabularData getJmonitorDataList() throws JMException;

    long getResetCount();

    void reset();

}
