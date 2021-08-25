//package cn.jmonitor.monitor4j.plugin.spring;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import cn.jmonitor.monitor4j.plugin.web.WebEntryLocal;
//import cn.jmonitor.monitor4j.plugin.web.WebUrlDataCalHelper;
//import cn.jmonitor.monitor4j.plugin.web.profile.WebUrlProfileDataManager;
//import cn.jmonitor.monitor4j.utils.FileUtils;
//import cn.jmonitor.monitor4j.utils.SpringMethodUtils;
//
//public class JMonitorMethodInterceptor implements MethodInterceptor {
//
//    private static Log LOG = LogFactory.getLog(JMonitorMethodInterceptor.class);
//
//    private final SpringMethodDataManager springMethodDataManager = SpringMethodDataManager.getInstance();
//
//    private final WebUrlProfileDataManager webUrlProfileDataManager = WebUrlProfileDataManager.getInstance();
//
//    // 每个时间段采集的最大数量,超过这个数量将抛弃
//    private int maxCount = 2000;
//
//    private int maxStackTraceLength = 2000;
//
//    static {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("cn.jmonitor.monitor4j.plugin.spring.JMonitorMethodInterceptor started.");
//        }
//        FileUtils.appendToLog("cn.jmonitor.monitor4j.plugin.spring.JMonitorMethodInterceptor started.");
//    }
//
//    /**
//     * 方法拦截不了final,static的方法
//     */
//    @Override
//    public Object invoke(MethodInvocation invocation) throws Throwable {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("map size:" + springMethodDataManager.getDataMap().size());
//            LOG.debug(invocation);
//        }
//        if (springMethodDataManager.getDataMap().size() > maxCount) {
//            LOG.error("JMonitorMethodInterceptor'count has over:" + maxCount);
//            return invocation.proceed();
//        }
//        SpringMethodItemKey itemKey = SpringMethodUtils.getMethodInfo(invocation);
//        SpringMethodItemValue springMethodItemValue = handleBefore(itemKey);
//
//        SpringMethodItemKey itemKeyForProfile = itemKey.clone();
//        SpringMethodItemValue springMethodItemValueForProfile = handleBeforeForProfile(itemKeyForProfile);
//
//        long startNano = System.nanoTime();
//        boolean error = false;
//        String errorMsg = null;
//        String errorSackTrace = null;
//        try {
//            return invocation.proceed();
//        } catch (Throwable ex) {
//            error = true;
//            errorMsg = ex.getMessage();
//            if (ex != null) {
//                StringWriter buf = new StringWriter();
//                ex.printStackTrace(new PrintWriter(buf));
//                errorSackTrace = buf.toString();
//                // 截断消息,防止数据过长
//                if (errorSackTrace.length() > maxStackTraceLength) {
//                    errorSackTrace.substring(0, maxStackTraceLength);
//                }
//            }
//            throw ex;
//        } finally {
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("springMethodItemValue finally handle.");
//            }
//            handleAfter(springMethodItemValue, startNano, error, errorMsg, errorSackTrace);
//            handleAfter(springMethodItemValueForProfile, startNano, error, errorMsg, errorSackTrace);
//        }
//    }
//
//    private SpringMethodItemValue handleBefore(SpringMethodItemKey itemKey) {
//        SpringMethodItemValue itemValue = springMethodDataManager.getDataMap().get(itemKey);
//        if (null == itemValue) {
//            itemValue = new SpringMethodItemValue(itemKey);
//            springMethodDataManager.getDataMap().put(itemKey, itemValue);
//        }
//        itemValue.incrementRunningCount();
//        return itemValue;
//    }
//
//    private void handleAfter(SpringMethodItemValue springMethodItemValue, long startNano, boolean error,
//            String errorMsg, String errorSackTrace) {
//        if (null == springMethodItemValue) {
//            return;
//        }
//        springMethodItemValue.handleAfter(startNano, error, errorMsg, errorSackTrace);
//    }
//
//    private SpringMethodItemValue handleBeforeForProfile(SpringMethodItemKey itemKey) {
//        WebEntryLocal currentEntry = WebUrlDataCalHelper.currentWebEntry.get();
//        if (null == currentEntry) {
//            return null;
//        }
//        String currentUrl = currentEntry.getUrl();
//        if (null == currentUrl) {
//            return null;
//        }
//        ConcurrentMap<SpringMethodItemKey, SpringMethodItemValue> springDataMap = webUrlProfileDataManager
//                .getProfileSpringDataMap().get(currentUrl);
//        if (null == springDataMap) {
//            springDataMap = new ConcurrentHashMap<SpringMethodItemKey, SpringMethodItemValue>();
//            webUrlProfileDataManager.getProfileSpringDataMap().put(currentUrl, springDataMap);
//        }
//        SpringMethodItemValue itemValue = springDataMap.get(itemKey);
//        if (null == itemValue) {
//            itemValue = new SpringMethodItemValue(itemKey);
//            springDataMap.put(itemKey, itemValue);
//        }
//        itemValue.incrementRunningCount();
//        return itemValue;
//    }
//
//    public void setMaxCount(int maxCount) {
//        if (maxCount <= 0) {
//            return;
//        }
//        this.maxCount = maxCount;
//    }
//
//    public void setMaxStackTraceLength(int maxStackTraceLength) {
//        if (maxStackTraceLength <= 0) {
//            return;
//        }
//        this.maxStackTraceLength = maxStackTraceLength;
//    }
//}
