package tmall.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;
import tmall.bean.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

public class ForeAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 不需要过滤的页面数组
        String[] noNeedAuthPage = {
                "home",
                "checkLogin",
                "register",
                "loginAjax",
                "login",
                "product",
                "category",
                "search"
        };

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        uri = StringUtils.remove(uri, contextPath);

        // 只有以fore开头的才被拦截，像css、js文件不会被拦截
        if (uri.startsWith("/fore") && !uri.startsWith("foreServlet")) {
            String method = StringUtils.substringAfterLast(uri, "/fore");
            // 如果不包含这些页面，说明访问的是必须登录的页面
            if (!Arrays.asList(noNeedAuthPage).contains(method)) {
                User user = (User) request.getSession().getAttribute("user");
                // 验证是否登录，为null则未登录，跳转到登录界面
                if (null == user) {
                    String requestURI = request.getRequestURI();
                    response.sendRedirect("login.jsp?url=" + URLEncoder.encode(requestURI));
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
