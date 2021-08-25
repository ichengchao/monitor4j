package cn.jmonitor.monitor4j.plugin.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public class WebUrlDataManager implements WebUrlDataManagerMBean {

    private static WebUrlDataManager instance = new WebUrlDataManager();

    private final AtomicLong resetCount = new AtomicLong();

    private final ConcurrentMap<String, WebUrlItem> webUrlMap = new ConcurrentHashMap<String, WebUrlItem>();

    public static final WebUrlDataManager getInstance() {
        return instance;
    }

    private WebUrlDataManager() {
        // never
    }

    @Override
    public TabularData getJmonitorDataList() throws JMException {
        CompositeType rowType = WebUrlItem.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);
        TabularType tabularType = new TabularType("URIStatisticList", "URIStatisticList", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);
        for (WebUrlItem webUrlItem : webUrlMap.values()) {
            data.put(webUrlItem.getCompositeData());
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
        webUrlMap.clear();
    }

    public ConcurrentMap<String, WebUrlItem> getWebUrlMap() {
        return webUrlMap;
    }

}
