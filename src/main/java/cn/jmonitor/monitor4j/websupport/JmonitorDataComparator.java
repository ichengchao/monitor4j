/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.websupport;

import java.util.Comparator;

import cn.jmonitor.monitor4j.utils.StringUtils;
import cn.jmonitor.monitor4j.websupport.items.BaseComparable;

/**
 * @author charles 2014年1月16日 下午2:14:01
 */
public class JmonitorDataComparator implements Comparator<BaseComparable> {

    private String compareField;
    private String order;

    /**
     * @param compareField 排序的字段
     * @param order        排序的方向
     */
    public JmonitorDataComparator(String compareField, String order) {
        super();
        this.compareField = compareField;
        this.order = order;
    }

    @Override
    public int compare(BaseComparable o1, BaseComparable o2) {
        if (StringUtils.isBlank(compareField) || null == o1 || null == o2) {
            return 0;
        }
        Long c = 0L;
        if ("count".equals(compareField)) {
            c = o1.getCount() - o2.getCount();
        }
        if ("DESC".equals(order)) {
            return 0 - c.intValue();
        }
        return c.intValue();
    }

}
