package tmall.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackServletFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        // 先转换成HttpServletRequest和HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 获取根路径
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        // 将根路径从uri中移除
        uri = StringUtils.remove(uri, contextPath);
        // 只过滤/admin开头的，其他的图片，资源不过滤
        if (uri.startsWith("/admin_")) {
            // 获取"_"之间的子串，拼接上Servlet就是资源路径
            String servletPath = StringUtils.substringBetween(uri, "_", "_") + "Servlet";
            // 获取方法名
            String method = StringUtils.substringAfterLast(uri, "_");
            request.setAttribute("method", method);
            // 内部资源转发，不需要加admin，直接资源名称即可
            req.getRequestDispatcher("/" + servletPath).forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
