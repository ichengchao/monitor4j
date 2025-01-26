package cn.jmonitor.monitor4j.websupport;

import java.io.IOException;
import java.util.Date;

import cn.jmonitor.monitor4j.utils.IOUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MonitorWebViewServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static String RESOURCE_PATH = "support";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        // 根目录访问的时候
        if ("".equals(path)) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(new Date());
            response.flushBuffer();
            return;
        }

        response.setCharacterEncoding("UTF-8");
        if (path.endsWith(".do")) {
            MonitorWebDataFacade.jsonDataDispather(request, response, path);
            return;
        }

        returnResourceFile(path, uri, response);
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
            response.sendRedirect(uri + "/index.html");
            return;
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

}
