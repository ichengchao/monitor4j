package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;

public class SpringMethodSingleForWeb {

    private SpringMethodInfo methodInfo;
    private SpringMethodStat springMethodStat;
    private Date timeStamp;

    public SpringMethodSingleForWeb(SpringMethodInfo methodInfo, SpringMethodStat springMethodStat, Date timeStamp) {
        super();
        this.methodInfo = methodInfo;
        this.springMethodStat = springMethodStat;
        this.timeStamp = timeStamp;
    }

    public SpringMethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(SpringMethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public SpringMethodStat getSpringMethodStat() {
        return springMethodStat;
    }

    public void setSpringMethodStat(SpringMethodStat springMethodStat) {
        this.springMethodStat = springMethodStat;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
