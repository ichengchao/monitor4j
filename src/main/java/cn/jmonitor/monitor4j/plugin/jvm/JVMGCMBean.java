/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.jvm;

public interface JVMGCMBean {

    long getYoungGCCollectionCount();

    long getYoungGCCollectionTime();

    long getFullGCCollectionCount();

    long getFullGCCollectionTime();

    // 下面的数字是做过差计算的,启动后的第二次开始才能做差值
    long getSpanYoungGCCollectionCount();

    long getSpanYoungGCCollectionTime();

    long getSpanFullGCCollectionCount();

    long getSpanFullGCCollectionTime();

}
