package cn.jmonitor.monitor4j.plugin.ip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public class WebIPDataManager implements WebIPDataManagerMBean {

    private static WebIPDataManager instance = new WebIPDataManager();

    private final AtomicLong resetCount = new AtomicLong();

    private final ConcurrentMap<String, WebIPItem> webIPMap = new ConcurrentHashMap<String, WebIPItem>();

    public static final WebIPDataManager getInstance() {
        return instance;
    }

    private WebIPDataManager() {
        // never
    }

    @Override
    public TabularData getJmonitorDataList() throws JMException {
        CompositeType rowType = WebIPItem.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);
        TabularType tabularType = new TabularType("IPStatisticList", "IPStatisticList", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);
        for (WebIPItem webIPItem : webIPMap.values()) {
            data.put(webIPItem.getCompositeData());
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
        webIPMap.clear();
    }

    public ConcurrentMap<String, WebIPItem> getWebIPMap() {
        return webIPMap;
    }

}
