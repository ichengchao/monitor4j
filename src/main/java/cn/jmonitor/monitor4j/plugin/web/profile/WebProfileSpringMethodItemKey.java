/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.web.profile;

import cn.jmonitor.monitor4j.plugin.spring.SpringMethodItemKey;

/**
 * @author charles 2013年12月25日 下午1:43:06
 */
public class WebProfileSpringMethodItemKey extends SpringMethodItemKey {

    public WebProfileSpringMethodItemKey(String url, String className, String method) {
        super(className, method);
        this.url = url;
    }

    private final String url;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebProfileSpringMethodItemKey other = (WebProfileSpringMethodItemKey) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
