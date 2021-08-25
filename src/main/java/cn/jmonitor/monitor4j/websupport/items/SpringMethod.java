package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringMethod {

    private Map<SpringMethodInfo, SpringMethodStat> methodMap = new ConcurrentHashMap<SpringMethodInfo, SpringMethodStat>();
    private Date timeStamp;

    public Map<SpringMethodInfo, SpringMethodStat> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(Map<SpringMethodInfo, SpringMethodStat> methodMap) {
        this.methodMap = methodMap;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
