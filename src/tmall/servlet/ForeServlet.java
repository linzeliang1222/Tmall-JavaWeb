package tmall.servlet;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.util.HtmlUtils;
import tmall.bean.*;
import tmall.comparator.*;
import tmall.dao.OrderDAO;
import tmall.dao.ProductImageDAO;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class ForeServlet extends BaseForeServlet {

    /**
     * 填充分类和产品关系，跳转到主页
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Category> cs = categoryDAO.list();
        productDAO.fill(cs);
        productDAO.fillByRow(cs);
        request.setAttribute("cs", cs);

        return "home.jsp";
    }

    /**
     * 注册功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String register(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        // 转义名字，防止恶意注册
        name = HtmlUtils.htmlEscape(name);

        // 判断用户名是否存在
        boolean isExistName = userDAO.isExist(name);
        if (isExistName) {
            request.setAttribute("msg", "用户名已经被使用,不能使用");
            return "register.jsp";
        }

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        userDAO.add(user);

        return "@registerSuccess.jsp";
    }

    /**
     * 登录功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String login(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        name = HtmlUtils.htmlEscape(name);

        User user = userDAO.get(name, password);

        // 为null说明用户不存在
        if (null == user) {
            request.setAttribute("msg", "账号或密码错误");
            return "login.jsp";
        }

        request.getSession().setAttribute("user", user);

        String url = request.getParameter("url");
        if (null != url && 0 != url.length()) {
            String redirectURL = URLDecoder.decode(url);
            return "@" + redirectURL;
        }
        return "@" + request.getContextPath();
    }

    /**
     * 注销功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String logout(HttpServletRequest request, HttpServletResponse response, Page page) {
        request.getSession().removeAttribute("user");
        request.getSession().removeAttribute("ois");

        return "@forehome";
    }

    /**
     * 填充产品信息功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String product(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);

        // 获取产品的图片
        List<ProductImage> productSingleImages = productImageDAO.list(p, ProductImageDAO.type_single);
        List<ProductImage> productDetailImages = productImageDAO.list(p, ProductImageDAO.type_detail);
        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);
        productDAO.setSaleAndReviewNumber(p);

        // 获取产品的属性
        List<PropertyValue> pvs = propertyValueDAO.list(p.getId());

        // 获取产品的评论
        List<Review> reviews = reviewDAO.list(p.getId());

        request.setAttribute("reviews", reviews);
        request.setAttribute("p", p);
        request.setAttribute("pvs", pvs);

        return "product.jsp";
    }

    /**
     * 检查登录状态功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");

        if (null != user) {
            return "%success";
        }

        return "%fail";
    }

    /**
     * 模态登录检查功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String loginAjax(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        name = HtmlUtils.htmlEscape(name);

        User user = userDAO.get(name, password);
        if (null == user) {
            return "%fail";
        }

        request.getSession().setAttribute("user", user);

        return "%success";
    }

    /**
     * 产品排序功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String category(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);

        // 填充分类下的产品
        productDAO.fill(c);
        // 设置某分类下的产品的销售数量和评论数量
        productDAO.setSaleAndReviewNumber(c.getProducts());

        String sort = request.getParameter("sort");
        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        request.setAttribute("c", c);

        return "category.jsp";
    }

    /**
     * 搜索功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String search(HttpServletRequest request, HttpServletResponse response, Page page) {
        String keyword = request.getParameter("keyword");
        // 查询前20个产品
        List<Product> ps = productDAO.search(keyword, 0, 20);
        productDAO.setSaleAndReviewNumber(ps);
        request.setAttribute("ps", ps);

        return "searchResult.jsp";
    }

    /**
     * 立即购买功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
        // 产品id
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);
        // 产品数量
        int num = Integer.parseInt(request.getParameter("num"));
        // 一个产品对呀的订单项id
        int oiid = 0;
        // 获取用户信息
        User user = (User) request.getSession().getAttribute("user");

        // 直接点击购买，就是直接创建新的订单项，不查询购物车是否存在该产品
        OrderItem oi = new OrderItem();
        oi.setUser(user);
        oi.setNumber(num);
        oi.setProduct(p);
        orderItemDAO.add(oi);
        oiid = oi.getId();

        return "@forebuy?oiid=" + oiid;
    }

    /**
     * 购买功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String buy(HttpServletRequest request, HttpServletResponse response, Page page) {
        String[] oiids = request.getParameterValues("oiid");
        List<OrderItem> ois = new ArrayList<>();
        // 计算订单总价
        float total = 0;

        // 遍历订单的每一个订单项（即产品）
        for (String strid : oiids) {
            int oiid = Integer.parseInt(strid);
            OrderItem oi = orderItemDAO.get(oiid);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            ois.add(oi);
        }

        request.getSession().setAttribute("ois", ois);
        request.setAttribute("total", total);

        return "buy.jsp";
    }

    /**
     * 添加购物车功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        // 获取加入购物车的产品id
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        User user = (User) request.getSession().getAttribute("user");
        Product p = productDAO.get(pid);

        // 用于判断购物车是否存在该产品
        boolean found = false;
        // 获取用户
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == p.getId()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemDAO.update(oi);
                found = true;
                break;
            }
        }

        // 如果购物车里面没有该产品，就新建一个orderItem，否则只需更新
        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setNumber(num);
            oi.setProduct(p);
            oi.setUser(user);
            orderItemDAO.add(oi);
        }

        return "%success";
    }

    /**
     * 查看购物车功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        request.setAttribute("ois", ois);

        return "cart.jsp";
    }

    /**
     * 修改购物车商品功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        User user = (User) request.getSession().getAttribute("user");

        Product p = productDAO.get(pid);
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == p.getId()) {
                oi.setNumber(num);
                orderItemDAO.update(oi);
                break;
            }
        }

        return "%success";
    }

    /**
     * 删除购物车商品功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        User user = (User) request.getSession().getAttribute("user");

        // 只能删除自己购物车的，不能删除别人购物车的产品
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getId() == oiid) {
                orderItemDAO.delete(oiid);
                return "%success";
            }
        }

        return "%fail";
    }

    /**
     * 生成订单功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = (List<OrderItem>) request.getSession().getAttribute("ois");
        if (null == ois) {
            return "@forehome";
        }

        String address = request.getParameter("address");
        String post = request.getParameter("post");
        String receiver = request.getParameter("receiver");
        String mobile = request.getParameter("mobile");
        String userMessage = request.getParameter("userMessage");

        // 创建订单
        Order order = new Order();
        // 生成21位订单号
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(1000, 9999);

        // 写入信息
        order.setOrderCode(orderCode);
        order.setAddress(address);
        order.setPost(post);
        order.setReceiver(receiver);
        order.setMobile(mobile);
        order.setUserMessage(userMessage);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderDAO.waitPay);
        orderDAO.add(order);

        float total = 0;
        for (OrderItem oi : ois) {
            // 设置每一个订单项所属的订单
            oi.setOrder(order);
            orderItemDAO.update(oi);
            // 计算总价格
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }

        return "@forealipay?oid=" + order.getId() + "&total=" + total;
    }

    /**
     * 跳转订单支付页面功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String alipay(HttpServletRequest request, HttpServletResponse response, Page page) {
        return "alipay.jsp";
    }

    /**
     * 订单支付功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String payed(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDAO.get(oid);

        if (order.getUser().getId() != user.getId()) {
            return "@forehome";
        }

        // 修改订单状态
        order.setStatus(OrderDAO.waitDelivery);
        order.setPayDate(new Date());
        // 更新
        orderDAO.update(order);

        request.setAttribute("o", order);

        return "payed.jsp";
    }

    /**
     * 我的订单功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String bought(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<Order> os = orderDAO.list(user.getId(), OrderDAO.delete);

        // 为订单填充订单项
        orderItemDAO.fill(os);

        request.setAttribute("os", os);

        return "bought.jsp";
    }

    /**
     * 确认付款到卖家功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String confirmPay(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);

        // 验证订单是否属于当前用户
        if (o.getUser().getId() != user.getId()) {
            return "@forehome";
        }

        // 为订单填充订单项
        orderItemDAO.fill(o);

        request.setAttribute("o", o);

        return "confirmPay.jsp";
    }

    /**
     * 确认收货功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String orderConfirmed(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);

        if (o.getUser().getId() != user.getId()) {
            return "@forehome";
        }

        o.setStatus(OrderDAO.waitReview);
        o.setConfirmDate(new Date());
        orderDAO.update(o);

        return "orderConfirmed.jsp";
    }

    /**
     * 删除订单功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);

        if (o.getUser().getId() != user.getId()) {
            return "@forehome";
        }

        o.setStatus(OrderDAO.delete);
        orderDAO.update(o);

        return "%success";
    }

    /**
     * 进行评价功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String review(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);

        if (o.getUser().getId() != user.getId()) {
            return "@forehome";
        }

        orderItemDAO.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewDAO.list(p.getId());
        productDAO.setSaleAndReviewNumber(p);

        request.setAttribute("p", p);
        request.setAttribute("o", o);
        request.setAttribute("reviews", reviews);

        return "review.jsp";
    }

    /**
     * 确认评价功能
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String doreview(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));
        int pid = Integer.parseInt(request.getParameter("pid"));
        String content = request.getParameter("content");
        content = HtmlUtils.htmlEscape(content);
        Order o = orderDAO.get(oid);
        Product p = productDAO.get(pid);

        if (o.getUser().getId() != user.getId()) {
            return "@forehome";
        }

        // 订单状态更新
        o.setStatus(OrderDAO.finish);
        orderDAO.update(o);

        // 创建评论
        Review review = new Review();
        review.setUser(user);
        review.setProduct(p);
        review.setContent(content);
        review.setCreateDate(new Date());
        reviewDAO.add(review);

        // 产品的评论数加一
        p.setReviewCount(p.getReviewCount() + 1);
        System.out.println(p.getReviewCount());

        return "@forereview?oid=" + oid + "&showonly=true";
    }
}
