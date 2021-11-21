package tmall.filter;

import org.apache.commons.lang3.StringUtils;
import tmall.bean.Category;
import tmall.bean.OrderItem;
import tmall.bean.User;
import tmall.dao.CategoryDAO;
import tmall.dao.OrderItemDAO;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ForeServletFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String contextPath = request.getContextPath();
        request.getServletContext().setAttribute("contextPath", contextPath);

        // 获取用户，计算购物车总商品数量个数
        User user = (User) request.getSession().getAttribute("user");
        int cartTotalItemNumber = 0;
        if (null != user) {
            List<OrderItem> ois = new OrderItemDAO().listByUser(user.getId());
            for (OrderItem oi : ois) {
                cartTotalItemNumber += oi.getNumber();
            }
        }
        request.setAttribute("cartTotalItemNumber", cartTotalItemNumber);

        // 获取分类
        List<Category> cs = (List<Category>) request.getAttribute("cs");
        if (null == cs) {
            cs = new CategoryDAO().list();
            request.setAttribute("cs", cs);
        }

        // 解析uri，获取请求的方法
        String uri = request.getRequestURI();
        uri = StringUtils.remove(uri, contextPath);
        if (uri.startsWith("/fore") && !uri.startsWith("/foreServlet")) {
            String method = StringUtils.substringAfterLast(uri, "/fore");
            request.setAttribute("method", method);
            request.getRequestDispatcher("/foreServlet").forward(request, response);
            return;
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
