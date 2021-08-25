/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package cn.jmonitor.monitor4j.plugin.spring;

/**
 * spring 方法拦截的key(类名+方法)
 * 
 * @author charles 2013年12月25日 下午1:43:06
 */
public class SpringMethodItemKey implements Cloneable {

    private String className;
    private final String method;

    public SpringMethodItemKey(String className, String method) {
        super();
        this.className = className;
        this.method = method;
    }

    @Override
    public SpringMethodItemKey clone() throws CloneNotSupportedException {
        return (SpringMethodItemKey) super.clone();
    }

    public String getClassName() {
        return className;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpringMethodItemKey other = (SpringMethodItemKey) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        return true;
    }

}
