package cn.jmonitor.monitor4j.websupport;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jmonitor.monitor4j.utils.IOUtils;

/**
 * fix bug: html文件增加content-type
 * 类MonitorWebViewFilter.java的实现描述：测试的filter,可以避免.do被web框架拦截
 * 
 * @author charles 2014年4月10日 上午10:34:15
 */
public class MonitorWebViewFilter implements Filter {

    private final static String RESOURCE_PATH = "support";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }        String uri = contextPath + servletPath;
        String path;
        if (servletPath.startsWith("/")) {
            String tmpServletPath = servletPath.substring(1);
            path = tmpServletPath.substring(tmpServletPath.indexOf("/"));
        } else {
            path = servletPath.substring(servletPath.indexOf("/"));
        }

        // 根目录访问的时候
        if ("".equals(path)) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(new Date());
            response.flushBuffer();
            return;
        }

        if (path.endsWith(".do")) {
            MonitorWebDataFacade.jsonDataDispather(request, response, path);
            return;
        }

        returnResourceFile(path, uri, response);

    }

    @Override
    public void destroy() {
        // nothing

    }

    private void returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException, IOException {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            byte[] bytes = IOUtils.readByteArrayFromResource(RESOURCE_PATH + fileName);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }
            return;
        }

        String text = IOUtils.readFromResource(RESOURCE_PATH + fileName);
        if (text == null) {
            response.getWriter().write("error url");
            response.flushBuffer();
            return;
        }
        if (fileName.endsWith(".html")) {
            response.setContentType("text/html;charset=utf-8");
        } else if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

}
