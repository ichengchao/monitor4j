package cn.jmonitor.monitor4j.plugin.web.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import cn.jmonitor.monitor4j.plugin.spring.SpringMethodItemKey;
import cn.jmonitor.monitor4j.plugin.spring.SpringMethodItemValue;

public class WebUrlProfileDataManager implements WebUrlProfileDataManagerMBean {

    private static WebUrlProfileDataManager instance = new WebUrlProfileDataManager();

    private final AtomicLong resetCount = new AtomicLong();

    private final ConcurrentMap<String, ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue>> profileSpringDataMap = new ConcurrentHashMap<String, ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue>>();

    private enum ProfileType {
        ibatis, spring
    }

    public static final WebUrlProfileDataManager getInstance() {
        return instance;
    }

    private WebUrlProfileDataManager() {
        // never
    }

    public ConcurrentMap<String, ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue>> getProfileSpringDataMap() {
        return profileSpringDataMap;
    }

    @Override
    public long getResetCount() {
        return resetCount.get();
    }

    @Override
    public void reset() {
        resetCount.incrementAndGet();
        profileSpringDataMap.clear();
    }

    @Override
    public TabularData getJmonitorDataList() throws JMException {
        CompositeType rowType = getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);
        TabularType tabularType = new TabularType("WebProfileList", "WebProfileList", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

//        // ibatis
//        for (Map.Entry<String, ConcurrentMap<String, IbatisDataItem>> entry : profileIbatisDataMap.entrySet()) {
//            for (Map.Entry<String, IbatisDataItem> subEntry : entry.getValue().entrySet()) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("URL", entry.getKey());
//                map.put("Name", subEntry.getValue().getId() + " [" + subEntry.getValue().getResource() + "]");
//                map.put("Type", ProfileType.ibatis.toString());
//                map.put("Count", subEntry.getValue().getCount());
//                map.put("ConcurrentMax", subEntry.getValue().getConcurrentMax());
//
//                map.put("RunningCount", subEntry.getValue().getRunningCount());
//                map.put("NanoTotal", subEntry.getValue().getNanoTotal());
//                map.put("NanoMax", subEntry.getValue().getNanoMax());
//                map.put("ErrorCount", subEntry.getValue().getErrorCount());
//                map.put("LastErrorMessage", subEntry.getValue().getLastErrorMessage());
//
//                map.put("LastErrorTime", subEntry.getValue().getLastErrorTime());
//                data.put(new CompositeDataSupport(getCompositeType(), map));
//            }
//        }

        // spring
        for (Map.Entry<String, ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue>> entry : profileSpringDataMap
                .entrySet()) {
            for (Map.Entry<SpringMethodItemKey, SpringMethodItemValue> subEntry : entry.getValue().entrySet()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("URL", entry.getKey());
                map.put("Name", subEntry.getKey().getClassName() + "." + subEntry.getKey().getMethod());
                map.put("Type", ProfileType.spring.toString());
                map.put("Count", subEntry.getValue().getInvokCount());
                map.put("ConcurrentMax", subEntry.getValue().getConcurrentMax());

                map.put("RunningCount", subEntry.getValue().getRunningCount());
                map.put("NanoTotal", subEntry.getValue().getNanoTotal());
                map.put("NanoMax", subEntry.getValue().getNanoMax());
                map.put("ErrorCount", subEntry.getValue().getErrorCount());
                map.put("LastErrorMessage", subEntry.getValue().getLastErrorMessage());

                map.put("LastErrorTime", subEntry.getValue().getLastErrorTime());
                data.put(new CompositeDataSupport(getCompositeType(), map));
            }
        }

        return data;
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {
        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }
        OpenType<?>[] indexTypes = new OpenType<?>[] {
                // 1
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.INTEGER, //

                // 2
                SimpleType.INTEGER, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.STRING, //

                // 3
                SimpleType.DATE, //
        };
        String[] indexNames = {
                // 1
                "URL", //
                "Name", //
                "Type", //
                "Count", //
                "ConcurrentMax", //

                // 2
                "RunningCount", //
                "NanoTotal", //
                "NanoMax", //
                "ErrorCount", //
                "LastErrorMessage", //

                // 3
                "LastErrorTime", //
        };
        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("WebProfileList", "web profile list", indexNames, indexDescriptions,
                indexTypes);
        return COMPOSITE_TYPE;
    }

}
