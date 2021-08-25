package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebProfile {

    private Map<WebProfileInfo, WebProfileStat> map = new ConcurrentHashMap<WebProfileInfo, WebProfileStat>();
    private Date timeStamp;

    public Map<WebProfileInfo, WebProfileStat> getMap() {
        return map;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
