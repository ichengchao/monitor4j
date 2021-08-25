package cn.jmonitor.monitor4j.websupport;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jmonitor.monitor4j.utils.JsonUtils;
import cn.jmonitor.monitor4j.websupport.collector.ExceptionCollector;
import cn.jmonitor.monitor4j.websupport.collector.JvmCollector;
import cn.jmonitor.monitor4j.websupport.collector.SpringMethodCollector;
import cn.jmonitor.monitor4j.websupport.collector.SqlCollector;
import cn.jmonitor.monitor4j.websupport.collector.WebIPCollector;
import cn.jmonitor.monitor4j.websupport.collector.WebProfileCollector;
import cn.jmonitor.monitor4j.websupport.collector.WebUrlCollector;
import cn.jmonitor.monitor4j.websupport.items.LogInfoSingleInfoForWeb;
import cn.jmonitor.monitor4j.websupport.items.SpringMethodSingleForWeb;
import cn.jmonitor.monitor4j.websupport.items.WebSingleIPForWeb;
import cn.jmonitor.monitor4j.websupport.items.WebSingleUrlForWeb;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MonitorWebDataFacade {

    private static Log logger = LogFactory.getLog(MonitorWebDataFacade.class);

    private static MonitorWebDataFacade instance = new MonitorWebDataFacade();

    private static final String suffix = ".do";

    private MonitorWebDataFacade() {

    }

    public static MonitorWebDataFacade getInstance() {
        return instance;
    }

    public static void outputToJSON(HttpServletResponse response, Object result) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            if (null != result) {
                response.getWriter().write(JsonUtils.toJsonString(result));
            }
            response.flushBuffer();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void jsonDataDispather(HttpServletRequest request, HttpServletResponse response, String url) {
        try {
            if (!url.endsWith(suffix) || !url.startsWith("/")) {
                throw new IllegalArgumentException("url must startsWith '/' and endsWith '.do',url:" + url);
            }
            String method = url.substring(1, url.length() - suffix.length());
            Class<MonitorWebDataFacade> clazz = MonitorWebDataFacade.class;
            Method methodBean = clazz.getDeclaredMethod(method,
                    new Class[] { HttpServletRequest.class, HttpServletResponse.class });
            methodBean.invoke(MonitorWebDataFacade.getInstance(), new Object[] { request, response });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void getMemoryData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            List<Map<String, Object>> list = JvmCollector.getJvmMemoryModelList();
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getLogInfoData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            String logInfo = ExceptionCollector.getLogInfoForHtml(timeInterval);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", logInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getLogInfoStrackTraceData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Long timestamp = Long.valueOf(request.getParameter("timestamp"));
            String exceptionType = request.getParameter("exceptionType");
            String methodName = request.getParameter("methodName");
            String logInfo = ExceptionCollector.getLogInfoStrackTraceForHtml(exceptionType, methodName, timestamp);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", logInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getLogInfoDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            String exceptionType = request.getParameter("exceptionType");
            String methodName = request.getParameter("methodName");
            List<LogInfoSingleInfoForWeb> list = ExceptionCollector.getLogInfoSingleInfoForWebList(exceptionType,
                    methodName);
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getSpringMethodForHtml(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            String info = SpringMethodCollector.getSpringMethodForHtml(timeInterval);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", info);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getSpringMethodDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            String className = request.getParameter("className");
            String method = request.getParameter("method");
            List<SpringMethodSingleForWeb> list = SpringMethodCollector.getSingleSpringMethodForWebList(className,
                    method);
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getSpringMethodErrorDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String className = request.getParameter("className");
            String method = request.getParameter("method");
            String errorInfo = SpringMethodCollector.getSpringMethodErrorForHtml(className, method);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", errorInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getWebUrlData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            String urlInfo = WebUrlCollector.getWebUrlForHtml(timeInterval);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", urlInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getWebUrlProfileData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            String url = request.getParameter("url");
            String webProfile = WebProfileCollector.getWebProfileForHtml(url, timeInterval);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", webProfile);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getUrlErrorDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String url = request.getParameter("url");
            String urlErrorInfo = WebUrlCollector.getWebUrlErrorForHtml(url);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", urlErrorInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getUrlDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            String url = request.getParameter("url");
            List<WebSingleUrlForWeb> list = WebUrlCollector.getWebSingleUrlForWebList(url);
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getWebIPData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            String ipInfo = WebIPCollector.getWebIPForHtml(timeInterval);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", ipInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getIPErrorDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String url = request.getParameter("ip");
            String ipErrorInfo = WebIPCollector.getWebIPErrorForHtml(url);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", ipErrorInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getIPDetailData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            String ip = request.getParameter("ip");
            List<WebSingleIPForWeb> list = WebIPCollector.getWebSingleIPForWebList(ip);
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getGCData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {

            List<Map<String, Object>> list = JvmCollector.getJvmGCModelList();
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public static void getThreadInfoData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            List<Map<String, Object>> list = JvmCollector.getJvmThreadInfoList();
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            if (timeInterval > list.size()) {
                timeInterval = list.size();
            }
            list = list.subList(list.size() - timeInterval, list.size());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", list);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

    //
    // public static void getThreadInfoDetailData(HttpServletRequest request,
    // HttpServletResponse response) {
    // Map<String, Object> result = new HashMap<String, Object>();
    // try {
    // Long timestamp = Long.valueOf(request.getParameter("timestamp"));
    // String detail = Collector.getJvmThreadInfoDetail(timestamp);
    // Map<String, Object> content = new HashMap<String, Object>();
    // content.put("data", detail);
    // result.put("content", content);
    // } catch (Exception e) {
    // result.put("success", false);
    // result.put("errorMsg", e.getMessage());
    // logger.error(e.getMessage());
    // }
    // outputToJSON(response, result);
    // }
    //
    public static void getJvmInfoData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String jvmInfo = JvmCollector.buildJvmInfoHtml();
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", jvmInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage(), e);
        }
        outputToJSON(response, result);
    }

//    public static void getJDBCInfoData(HttpServletRequest request, HttpServletResponse response) {
//        Map<String, Object> result = new HashMap<String, Object>();
//        try {
//            String jdbcInfo = DruidDataSourceCollector.getJDBCInfoHtml();
//            Map<String, Object> content = new HashMap<String, Object>();
//            content.put("data", jdbcInfo);
//            result.put("content", content);
//        } catch (Exception e) {
//            result.put("success", false);
//            result.put("errorMsg", e.getMessage());
//            logger.error(e.getMessage(), e);
//        }
//        outputToJSON(response, result);
//    }

    public static void getJdbcSqlData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer timeInterval = Integer.valueOf(request.getParameter("timeInterval"));
            if (null == timeInterval) {
                timeInterval = 180;
            }
            String sqlInfo = SqlCollector.getSqlIForHtml(timeInterval);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("data", sqlInfo);
            result.put("content", content);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
            logger.error(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
