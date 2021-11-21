package tmall.dao;

import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.Product;
import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: OrderItem对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-28
 */
public class OrderItemDAO {

    /**
     * 获取所有订单项的总数
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;

        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()
                ) {

            String sql = "SELECT COUNT(*) FROM orderItem";

            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    /**
     * 添加订单项（添加商品到购物车）
     *
     * @param: bean
     */
    public void add(OrderItem bean) {
        String sql = "INSERT INTO orderItem VALUES (NULL, ?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            pstmt.setInt(1, bean.getProduct().getId());
            // 订单存在才设置oid，否则设置为-1
            if (null != bean.getOrder()) {
                pstmt.setInt(2, bean.getOrder().getId());
            } else {
                pstmt.setInt(2, -1);
            }
            pstmt.setInt(3, bean.getUser().getId());
            pstmt.setInt(4, bean.getNumber());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新订单项（更新购物车）
     *
     * @param: bean
     */
    public void update(OrderItem bean) {
        String sql = "UPDATE orderItem SET pid = ?, oid = ?, uid = ?, number = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, bean.getProduct().getId());
            // 存在订单才设置oid，否则为-1
            if (null != bean.getOrder()) {
                pstmt.setInt(2, bean.getOrder().getId());
            } else {
                pstmt.setInt(2, -1);
            }
            pstmt.setInt(3, bean.getUser().getId());
            pstmt.setInt(4, bean.getNumber());
            pstmt.setInt(5, bean.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除购物车产品
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM orderitem WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过id获取订单项
     *
     * @param: id
     * @return: OrderItem
     */
    public OrderItem get(int id) {
        OrderItem bean = null;

        String sql = "SELECT * FROM orderItem WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new OrderItem();
                int pid = rs.getInt("pid");
                int oid = rs.getInt("oid");
                int uid = rs.getInt("uid");
                int number = rs.getInt("number");
                Product product = new ProductDAO().get(pid);
                Order order = null;
                if (-1 != oid) {
                    order = new OrderDAO().get(oid);
                }
                User user = new UserDAO().get(uid);

                bean.setId(id);
                bean.setProduct(product);
                bean.setOrder(order);
                bean.setUser(user);
                bean.setNumber(number);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 通过uid获取某用户的所有订单项
     *
     * @param: uid
     * @return: List<OrderItem>
     */
    public List<OrderItem> listByUser(int uid) {
        return listByUser(uid, 0, Short.MAX_VALUE);
    }

    /**
     * 获取uid下的指定范围的订单项集合
     *
     * @param: uid
     * @param: start
     * @param: count
     * @return: List<Order>
     */
    public List<OrderItem> listByUser(int uid, int start, int count) {
        List<OrderItem> beans = new ArrayList<>();

        // oid=-1说明产品只是被加入到购物车，但是还没创建订单付款
        String sql = "SELECT * FROM orderItem WHERE uid = ? AND oid = -1 ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, uid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);
                int pid = rs.getInt("pid");
                int oid = rs.getInt("oid");
                int number = rs.getInt("number");
                Product product = new ProductDAO().get(pid);
                Order order = new OrderDAO().get(oid);
                User user = new UserDAO().get(uid);

                bean.setId(id);
                bean.setProduct(product);
                bean.setOrder(order);
                bean.setUser(user);
                bean.setNumber(number);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 通过某个订单获取所有订单项
     *
     * @param: oid
     * @return: List<OrderItem>
     */
    public List<OrderItem> listByOrder(int oid) {
        return listByOrder(oid, 0, Short.MAX_VALUE);
    }

    /**
     * 通过某个订单获取指定范围的订单项集合
     *
     * @param: oid
     * @return: List<OrderItem>
     */
    public List<OrderItem> listByOrder(int oid, int start, int count) {
        List<OrderItem> beans = new ArrayList<>();

        String sql = "SELECT * FROM orderItem WHERE oid = ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, oid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);
                int pid = rs.getInt("pid");
                int uid = rs.getInt("uid");
                int number = rs.getInt("number");
                Product product = new ProductDAO().get(pid);
                Order order = null;
                if (-1 != oid) {
                    order = new OrderDAO().get(oid);
                }
                User user = new UserDAO().get(uid);

                bean.setId(id);
                bean.setProduct(product);
                bean.setOrder(order);
                bean.setUser(user);
                bean.setNumber(number);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 通过pid(产品id)获取所有订单项集合
     *
     * @param: pid
     * @return: List<OrderItem>
     */
    public List<OrderItem> listByProduct(int pid) {
        return listByProduct(pid, 0, Short.MAX_VALUE);
    }

    /**
     * 通过pid(产品id)获取指定范围的订单项集合
     *
     * @param: pid
     * @param: start
     * @param: count
     * @return: List<OrderItem>
     */
    public List<OrderItem> listByProduct(int pid, int start, int count) {
        List<OrderItem> beans = new ArrayList<>();

        String sql = "SELECT * FROM orderItem WHERE pid = ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, pid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);
                int oid = rs.getInt("oid");
                int uid = rs.getInt("uid");
                int number = rs.getInt("number");
                Product product = new ProductDAO().get(pid);
                Order order = null;
                if (-1 != oid) {
                    order = new OrderDAO().get(oid);
                }
                User user = new UserDAO().get(uid);

                bean.setId(id);
                bean.setProduct(product);
                bean.setOrder(order);
                bean.setUser(user);
                bean.setNumber(number);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 为一个订单设置订单项集合
     *
     * @param: o
     */
    public void fill(Order o) {
        // 先获取某一个订单对应的所有的订单项
        List<OrderItem> orderItemList = listByOrder(o.getId());
        // 一个订单的总价格
        float total = 0;
        // 一个订单的总商品数量
        int totalNumber = 0;

        // 将订单中的每一个订单项进行累加
        for (OrderItem orderItem : orderItemList) {
            total += (orderItem.getNumber() * orderItem.getProduct().getPromotePrice());
            totalNumber += orderItem.getNumber();
        }
        o.setTotal(total);
        o.setTotalNumber(totalNumber);
        o.setOrderItems(orderItemList);
    }

    /**
     * 为订单集合的每一个订单设置订单项集合
     *
     * @param: o
     */
    public void fill(List<Order> os) {
        for (Order o : os) {
            fill(o);
        }
    }

    /**
     * 获取某产品的销售量
     *
     * @param: pid
     * @return: int
     */
    public int getSaleCount(int pid) {
        int total = 0;

        // 销售量是已经下单购买了的，不包含在购物车中的，故oid != -1
        String sql = "SELECT SUM(number) FROM orderItem WHERE pid = ? AND oid != -1";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, pid);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }
}
