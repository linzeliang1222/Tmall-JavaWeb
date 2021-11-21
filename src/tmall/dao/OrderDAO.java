package tmall.dao;

import tmall.bean.Order;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: Order对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-26
 */
public class OrderDAO {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    /**
     * 查询总的订单数
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;

        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()
                ) {

            String sql = "SELECT COUNT(*) FROM order_";

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
     * 添加订单
     *
     * @param: bean
     */
    public void add(Order bean) {
        String sql = "INSERT INTO order_ VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            pstmt.setString(1, bean.getOrderCode());
            pstmt.setString(2, bean.getAddress());
            pstmt.setString(3, bean.getPost());
            pstmt.setString(4, bean.getReceiver());
            pstmt.setString(5, bean.getMobile());
            pstmt.setString(6, bean.getUserMessage());
            pstmt.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));
            pstmt.setTimestamp(8, DateUtil.d2t(bean.getPayDate()));
            pstmt.setTimestamp(9, DateUtil.d2t(bean.getDeliveryDate()));
            pstmt.setTimestamp(10, DateUtil.d2t(bean.getConfirmDate()));
            pstmt.setInt(11, bean.getUser().getId());
            pstmt.setString(12, bean.getStatus());

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
     * 更新订单信息
     *
     * @param: bean
     */
    public void update(Order bean) {
        String sql = "UPDATE order_ SET orderCode = ?, address = ?, post = ?, receiver = ?, mobile = ?, userMessage = ?, createDate = ?, payDate = ?" +
                ", deliveryDate = ?, confirmDate = ?, uid = ?, status = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setString(1, bean.getOrderCode());
            pstmt.setString(2, bean.getAddress());
            pstmt.setString(3, bean.getPost());
            pstmt.setString(4, bean.getReceiver());
            pstmt.setString(5, bean.getMobile());
            pstmt.setString(6, bean.getUserMessage());
            pstmt.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));
            pstmt.setTimestamp(8, DateUtil.d2t(bean.getPayDate()));
            pstmt.setTimestamp(9, DateUtil.d2t(bean.getDeliveryDate()));
            pstmt.setTimestamp(10, DateUtil.d2t(bean.getConfirmDate()));
            pstmt.setInt(11, bean.getUser().getId());
            pstmt.setString(12, bean.getStatus());
            pstmt.setInt(13, bean.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除订单
     */
    public void delete(int id) {
        String sql = "DELETE FROM order_ WHERE id = ?";

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
     * 根据id获取订单信息
     *
     * @param: id
     * @return: Order
     */
    public Order get(int id) {
        Order bean = null;

        String sql = "SELECT * FROM order_ WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new Order();
                String orderCode = rs.getString("orderCode");
                String address = rs.getString("address");
                String post = rs.getString("post");
                String receiver = rs.getString("receiver");
                String mobile = rs.getString("mobile");
                String userMessage = rs.getString("userMessage");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                Date payDate = DateUtil.t2d(rs.getTimestamp("payDate"));
                Date deliveryDate = DateUtil.t2d(rs.getTimestamp("deliveryDate"));
                Date confirmDate = DateUtil.t2d(rs.getTimestamp("confirmDate"));
                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                String status = rs.getString("status");

                bean.setId(id);
                bean.setOrderCode(orderCode);
                bean.setAddress(address);
                bean.setPost(post);
                bean.setReceiver(receiver);
                bean.setMobile(mobile);
                bean.setUserMessage(userMessage);
                bean.setCreateDate(createDate);
                bean.setPayDate(payDate);
                bean.setDeliveryDate(deliveryDate);
                bean.setConfirmDate(confirmDate);
                bean.setUser(user);
                bean.setStatus(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 获取所有订单
     *
     * @return: List<Order>
     */
    public List<Order> list() {
        return list(0, Short.MAX_VALUE);
    }

    /**
     * 获取全部订单的指定范围集合
     *
     * @param: start
     * @param: count
     * @return: List<Order>
     */
    public List<Order> list(int start, int count) {
        List<Order> beans = new ArrayList<>();

        String sql = "SELECT * FROM order_ ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, start);
            pstmt.setInt(2, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order bean = new Order();
                int id = rs.getInt(1);
                String orderCode = rs.getString("orderCode");
                String address = rs.getString("address");
                String post = rs.getString("post");
                String receiver = rs.getString("receiver");
                String mobile = rs.getString("mobile");
                String userMessage = rs.getString("userMessage");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                Date payDate = DateUtil.t2d(rs.getTimestamp("payDate"));
                Date deliveryDate = DateUtil.t2d(rs.getTimestamp("deliveryDate"));
                Date confirmDate = DateUtil.t2d(rs.getTimestamp("confirmDate"));
                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                String status = rs.getString("status");

                bean.setId(id);
                bean.setOrderCode(orderCode);
                bean.setAddress(address);
                bean.setPost(post);
                bean.setReceiver(receiver);
                bean.setMobile(mobile);
                bean.setUserMessage(userMessage);
                bean.setCreateDate(createDate);
                bean.setPayDate(payDate);
                bean.setDeliveryDate(deliveryDate);
                bean.setConfirmDate(confirmDate);
                bean.setUser(user);
                bean.setStatus(status);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 获取某用户的排除指定状态的订单
     *
     * @param: uid
     * @param: excludedStatus
     * @return: List<Order>
     */
    public List<Order> list(int uid, String excludedStatus) {
        return list(uid, excludedStatus, 0, Short.MAX_VALUE);
    }

    /**
     * 获取某用户排除指定状态的范围订单集合
     *
     * @param: uid
     * @param: excludedStatus
     * @param: start
     * @param: count
     * @return: List<Order>
     */
    public List<Order> list(int uid, String excludedStatus, int start, int count) {
        List<Order> beans = new ArrayList<>();

        String sql = "SELECT * FROM order_ WHERE uid = ? AND status != ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, uid);
            pstmt.setString(2, excludedStatus);
            pstmt.setInt(3, start);
            pstmt.setInt(4, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order bean = new Order();
                int id = rs.getInt(1);
                String orderCode = rs.getString("orderCode");
                String address = rs.getString("address");
                String post = rs.getString("post");
                String receiver = rs.getString("receiver");
                String mobile = rs.getString("mobile");
                String userMessage = rs.getString("userMessage");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                Date payDate = DateUtil.t2d(rs.getTimestamp("payDate"));
                Date deliveryDate = DateUtil.t2d(rs.getTimestamp("deliveryDate"));
                Date confirmDate = DateUtil.t2d(rs.getTimestamp("confirmDate"));
                User user = new UserDAO().get(uid);
                String status = rs.getString("status");

                bean.setId(id);
                bean.setOrderCode(orderCode);
                bean.setAddress(address);
                bean.setPost(post);
                bean.setReceiver(receiver);
                bean.setMobile(mobile);
                bean.setUserMessage(userMessage);
                bean.setCreateDate(createDate);
                bean.setPayDate(payDate);
                bean.setDeliveryDate(deliveryDate);
                bean.setConfirmDate(confirmDate);
                bean.setUser(user);
                bean.setStatus(status);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
