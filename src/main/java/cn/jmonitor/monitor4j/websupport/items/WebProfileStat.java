package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class WebProfileStat {

    @JSONField(name = "URL")
    private String url;

    @JSONField(name = "Type")
    private String type;

    @JSONField(name = "Name")
    private String name;

    // 总数
    @JSONField(name = "Count")
    private long count;

    @JSONField(name = "RunningCount")
    private int runningCount;
    // 最大并发

    @JSONField(name = "ConcurrentMax")
    private int concurrentMax;

    @JSONField(name = "ErrorCount")
    private long errorCount;

    // 总耗时
    @JSONField(name = "NanoTotal")
    private long nanoTotal;

    // 最大耗时
    @JSONField(name = "NanoMax")
    private long nanoMax;

    @JSONField(name = "LastErrorMessage")
    private String lastErrorMsg;

    @JSONField(name = "LastErrorTime")
    private Date lastErrorTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getConcurrentMax() {
        return concurrentMax;
    }

    public void setConcurrentMax(int concurrentMax) {
        this.concurrentMax = concurrentMax;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public long getNanoTotal() {
        return nanoTotal;
    }

    public void setNanoTotal(long nanoTotal) {
        this.nanoTotal = nanoTotal;
    }

    public long getNanoMax() {
        return nanoMax;
    }

    public void setNanoMax(long nanoMax) {
        this.nanoMax = nanoMax;
    }

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    public void setLastErrorMsg(String lastErrorMsg) {
        this.lastErrorMsg = lastErrorMsg;
    }

    public Date getLastErrorTime() {
        return lastErrorTime;
    }

    public void setLastErrorTime(Date lastErrorTime) {
        this.lastErrorTime = lastErrorTime;
    }

    public WebProfileInfo buildWebProfileInfo() {
        WebProfileInfo webProfileInfo = new WebProfileInfo(url, type, name);
        return webProfileInfo;
    }

}
