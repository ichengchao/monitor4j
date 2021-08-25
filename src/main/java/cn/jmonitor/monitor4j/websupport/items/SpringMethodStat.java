package cn.jmonitor.monitor4j.websupport.items;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class SpringMethodStat implements BaseComparable {

    @JSONField(name = "Class")
    private String className;

    @JSONField(name = "Method")
    private String method;

    @JSONField(name = "RunningCount")
    private int runningCount;
    // 最大并发

    @JSONField(name = "ConcurrentMax")
    private int concurrentMax;

    // 总数
    @JSONField(name = "Count")
    private long count;

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

    @JSONField(name = "LastErrorStackTrace")
    private String lastErrorStackTrace;

    @JSONField(name = "LastErrorTime")
    private Date lastErrorTime;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
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

    public String getLastErrorStackTrace() {
        return lastErrorStackTrace;
    }

    public void setLastErrorStackTrace(String lastErrorStackTrace) {
        this.lastErrorStackTrace = lastErrorStackTrace;
    }

    public Date getLastErrorTime() {
        return lastErrorTime;
    }

    public void setLastErrorTime(Date lastErrorTime) {
        this.lastErrorTime = lastErrorTime;
    }

    public SpringMethodInfo buildSpringMethodInfo() {
        SpringMethodInfo springMethodInfo = new SpringMethodInfo(className, method);
        return springMethodInfo;
    }

}
