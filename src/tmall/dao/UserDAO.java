package tmall.dao;

import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: User对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-21
 */
public class UserDAO {

    /**
     * 获取总的用户数
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;
        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()
        ) {

            String sql = "SELECT COUNT(*) FROM user";

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
     * 添加新用户
     *
     * @param: bean
     */
    public void add(User bean) {
        String sql = "INSERT INTO user VALUES (NULL, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getPassword());

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
     * 更新用户信息
     *
     * @param: bean
     */
    public void update(User bean) {
        String sql = "UPDATE user SET name = ?, password = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getPassword());
            pstmt.setInt(3, bean.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除用户
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM user WHERE id = ?";

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
     * 根据id获取用户信息
     *
     * @param: id
     * @return: User
     */
    public User get(int id) {
        User bean = null;

        String sql = "SELECT * FROM user WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new User();
                String name = rs.getString("name");
                bean.setName(name);
                String password = rs.getString("password");
                bean.setPassword(password);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 获取所有用户列表
     *
     * @return: List<User>
     */
    public List<User> list() {
        return list(0, Short.MAX_VALUE);
    }

    /**
     * 获取指定区域的用户集合
     *
     * @param: start
     * @param: count
     * @return: List<User>
     */
    public List<User> list(int start, int count) {
        List<User> beans = new ArrayList<>();

        String sql = "SELECT * FROM user ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, start);
            pstmt.setInt(2, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User bean = new User();
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String password = rs.getString("password");

                bean.setId(id);
                bean.setName(name);
                bean.setPassword(password);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 通过用户名查询用户是否存在
     *
     * @param: name
     * @return: boolean
     */
    public boolean isExist(String name) {
        User user = get(name);
        return user != null;
    }

    /**
     * 通过用户名获取用户信息
     *
     * @param: name
     * @return: User
     */
    public User get(String name) {
        User bean = null;

        String sql = "SELECT * FROM user WHERE name = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                bean = new User();

                int id = rs.getInt(1);
                String password = rs.getString("password");

                bean.setId(id);
                bean.setName(name);
                bean.setPassword(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 通过账号密码获取用户信息
     *
     * @param: name
     * @param: password
     * @return: User
     */
    public User get(String name, String password) {
        User bean = null;

        String sql = "SELECT * FROM user WHERE name = ? AND password = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, name);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new User();

                int id = rs.getInt(1);

                bean.setId(id);
                bean.setName(name);
                bean.setPassword(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }
}
