/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.spring;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

/**
 * @author charles 2013年12月25日 下午1:42:43
 */
public class SpringMethodDataManager implements SpringMethodDataManagerMBean {

    private static SpringMethodDataManager instance = new SpringMethodDataManager();

    private final AtomicLong resetCount = new AtomicLong();

    private final ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue> dataMap = new ConcurrentHashMap<SpringMethodItemKey, SpringMethodItemValue>();

    private static Date lastRestTime;

    private SpringMethodDataManager() {
        // never
    }

    public static final SpringMethodDataManager getInstance() {
        return instance;
    }

    public ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue> getDataMap() {
        return dataMap;
    }

    @Override
    public TabularData getJmonitorDataList() throws JMException {
        CompositeType rowType = SpringMethodItemValue.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);
        TabularType tabularType = new TabularType("MethodList", "MethodList", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);
        for (SpringMethodItemValue itemValue : dataMap.values()) {
            if (itemValue.getInvokCount() == 0) {
                continue;
            }
            data.put(itemValue.getCompositeData());
        }
        return data;
    }

    @Override
    public long getResetCount() {
        return resetCount.get();
    }

    @Override
    public void reset() {
        resetCount.incrementAndGet();
        // dataMap.clear();
        Iterator<SpringMethodItemValue> iterator = dataMap.values().iterator();
        while (iterator.hasNext()) {
            SpringMethodItemValue itemValue = iterator.next();
            if (itemValue.getRunningCount() == 0) {
                iterator.remove();
            } else {
                itemValue.cleardata();
            }
        }
        lastRestTime = new Date();
    }

    @Override
    public Date getLastResetTime() {
        return lastRestTime;
    }

}
