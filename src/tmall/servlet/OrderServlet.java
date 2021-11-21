package tmall.servlet;

import tmall.bean.Order;
import tmall.dao.OrderDAO;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public class OrderServlet extends BaseBackServlet {
    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        // nowhere
        return null;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        // nowhere
        return null;
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        // nowhere
        return null;
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        // nowhere
        return null;
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Order> os = orderDAO.list(page.getStart(), page.getCount());
        // 查询的时候才计算订单详细信息
        orderItemDAO.fill(os);
        int total = orderDAO.getTotal();
        page.setTotal(total);

        request.setAttribute("os", os);
        request.setAttribute("page", page);

        return "admin/listOrder.jsp";
    }

    /**
     * 确认收货
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String delivery(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Order o = orderDAO.get(id);
        // 确认收货日期
        o.setDeliveryDate(new Date());
        // 修改订单状态为待收货
        o.setStatus(OrderDAO.waitConfirm);
        orderDAO.update(o);

        return "@admin_order_list";
    }
}
