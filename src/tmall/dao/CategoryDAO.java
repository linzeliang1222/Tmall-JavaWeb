package tmall.dao;

import tmall.bean.Category;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Category对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-21
 */
public class CategoryDAO {

    /**
     * 获取总的分类数量
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;

        // try括号内的资源会在try语句结束后自动释放（前提是这些资源必须实现AutoCloseable接口）
        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()) {

            String sql = "SELECT COUNT(*) FROM category";
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
     * 添加新的分类名到数据库中
     *
     * @param: bean
     */
    public void add(Category bean) {
        String sql = "INSERT INTO category VALUES(NULL, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            pstmt.setString(1, bean.getName());

            pstmt.executeUpdate();

            // 获取主键
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                // 同时把数据库生成的id回写记录到category中
                int id = rs.getInt(1);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id更新分类的名称
     *
     * @param: bean
     */
    public void update(Category bean) {
        String sql = "UPDATE category SET name = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setString(1, bean.getName());
            pstmt.setInt(2, bean.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除分类
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM category WHERE id = ?";

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
     * 根据id获取分类
     *
     * @param: id
     * @return: Category
     */
    public Category get(int id) {
        Category bean = null;

        String sql = "SELECT * FROM category WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                bean = new Category();
                String name = rs.getString(2);
                bean.setName(name);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 获取所有分类
     *
     * @return: List<Category>
     */
    public List<Category> list() {
        // -1说明检索的行数为：0-last
        return list(0, Short.MAX_VALUE);
    }

    /**
     * 获取指定区间的分类
     *
     * @param: start
     * @param: count
     * @return: List<Category>
     */
    public List<Category> list(int start, int count) {
        List<Category> beans = new ArrayList<>();

        String sql = "SELECT * FROM category ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, start);
            pstmt.setInt(2, count);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Category bean = new Category();
                int id = rs.getInt(1);
                String name = rs.getString(2);
                bean.setId(id);
                bean.setName(name);
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
