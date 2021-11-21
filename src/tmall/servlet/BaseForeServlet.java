package tmall.servlet;

import tmall.dao.*;
import tmall.util.Page;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class BaseForeServlet extends HttpServlet {
    protected CategoryDAO categoryDAO = new CategoryDAO();
    protected OrderDAO orderDAO = new OrderDAO();
    protected OrderItemDAO orderItemDAO = new OrderItemDAO();
    protected ProductDAO productDAO = new ProductDAO();
    protected ProductImageDAO productImageDAO = new ProductImageDAO();
    protected PropertyDAO propertyDAO = new PropertyDAO();
    protected PropertyValueDAO propertyValueDAO = new PropertyValueDAO();
    protected ReviewDAO reviewDAO = new ReviewDAO();
    protected UserDAO userDAO = new UserDAO();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int start = 0;
            int count = 10;
            try {
                start = Integer.parseInt(request.getParameter("page.start"));
            } catch (Exception e) {

            }
            try {
                count = Integer.parseInt(request.getParameter("page.count"));
            } catch (Exception e) {

            }
            Page page = new Page(start, count);

            String method = (String) request.getAttribute("method");
            // 通过method获取方法
            Method m = this.getClass().getMethod(method, javax.servlet.http.HttpServletRequest.class, javax.servlet.http.HttpServletResponse.class, Page.class);
            // 获取跳转路径
            String redirect = m.invoke(this, request, response, page).toString();

            // 解析redirect
            if (redirect.startsWith("@")) {
                response.sendRedirect(redirect.substring(1));
            } else if (redirect.startsWith("%")) {
                response.getWriter().print(redirect.substring(1));
            } else {
                request.getRequestDispatcher(redirect).forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
