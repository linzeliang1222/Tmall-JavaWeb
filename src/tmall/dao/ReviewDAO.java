package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Review;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: Review对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-23
 */
public class ReviewDAO {

    /**
     * 获取总的评论数
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;

        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()
                ) {

            String sql = "SELECT COUNT(*) FROM review";

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
     * 根据pid获取某类产品下的总评论数
     *
     * @param: pid
     * @return: int
     */
    public int getTotal(int pid) {
        int total = 0;

        String sql = "SELECT COUNT(*) FROM review WHERE pid = ?";

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

    /**
     * 添加用户评论
     *
     * @param: bean
     */
    public void add (Review bean) {
        String sql = "INSERT INTO review VALUES (NULL, ?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            pstmt.setString(1, bean.getContent());
            pstmt.setInt(2, bean.getUser().getId());
            pstmt.setInt(3, bean.getProduct().getId());
            pstmt.setTimestamp(4, DateUtil.d2t(bean.getCreateDate()));

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
     * 更新评论
     *
     * @param: bean
     */
    public void update (Review bean) {
        String sql = "UPDATE review SET content = ?, uid = ?, pid = ?, createDate = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setString(1, bean.getContent());
            pstmt.setInt(2, bean.getUser().getId());
            pstmt.setInt(3, bean.getProduct().getId());
            pstmt.setTimestamp(4, DateUtil.d2t(bean.getCreateDate()));
            pstmt.setInt(5, bean.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除评论
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM review WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 通过id获取评论
     *
     * @param: id
     * @return: Review
     */
    public Review get(int id) {
        Review bean = null;

        String sql = "SELECT * FROM review WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new Review();
                int uid = rs.getInt("uid");
                int pid = rs.getInt("pid");
                User user = new UserDAO().get(uid);
                Product product = new ProductDAO().get(pid);
                String content = rs.getString("content");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(id);
                bean.setUser(user);
                bean.setProduct(product);
                bean.setContent(content);
                bean.setCreateDate(createDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 获取某产品的所有评论集合
     *
     * @param: pid
     * @return: List<Review>
     */
    public List<Review> list(int pid) {
        return list(pid, 0, Short.MAX_VALUE);
    }

    /**
     * 获取某产品的部分评论集合
     *
     * @param: pid
     * @param: start
     * @param: count
     * @return: List<Review>
     */
    public List<Review> list(int pid, int start, int count) {
        List<Review> beans = new ArrayList<>();

        String sql = "SELECT * FROM review WHERE pid = ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, pid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review bean = new Review();
                int id = rs.getInt(1);
                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                Product product = new ProductDAO().get(pid);
                String content = rs.getString("content");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(id);
                bean.setUser(user);
                bean.setProduct(product);
                bean.setContent(content);
                bean.setCreateDate(createDate);
                beans.add(bean);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 获取某产品的总评论数
     *
     * @param: pid
     * @return: int
     */
    public int getCount(int pid) {
        int total = 0;

        String sql = "SELECT COUNT(*) FROM review WHERE pid = ?";

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

    /**
     * 查询某产品的评论是否存在
     *
     * @param: content
     * @param: pid
     * @return: boolean
     */
    public boolean isExist(String content, int pid) {
        String sql = "SELECT * FROM review WHERE content = ? AND pid = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setString(1, content);
            pstmt.setInt(2, pid);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // 存在评论直接返回true
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 不存在返回false
        return false;
    }
}
