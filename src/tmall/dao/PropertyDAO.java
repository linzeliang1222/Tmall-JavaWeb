package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Property对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-22
 */
public class PropertyDAO {

    /**
     * 根据cid查询该cid对应的分类下的所有属性总数
     *
     * @param: cid
     * @return: int
     */
    public int getTotal(int cid) {
        int total = 0;

        String sql = "SELECT COUNT(*) FROM property WHERE cid = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, cid);

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
     * 添加新的属性，同时也要设置cid外键
     *
     * @param: bean
     */
    public void add(Property bean) {
        String sql = "INSERT INTO property VALUES (NULL, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            // 设置该属性的cid
            pstmt.setInt(1, bean.getCategory().getId());
            pstmt.setString(2, bean.getName());
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
     * 修改属性信息
     *
     * @param: bean
     */
    public void update(Property bean) {
        String sql = "UPDATE property SET name = ?, cid = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setString(1, bean.getName());
            pstmt.setInt(2, bean.getCategory().getId());
            pstmt.setInt(3, bean.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过id删除属性
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM property WHERE id = ?";

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
     * 通过id获取属性
     *
     * @param: id
     * @return: Property
     */
    public Property get(int id) {
        Property bean = null;

        String sql = "SELECT * FROM property WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new Property();
                int cid = rs.getInt("cid");
                Category category = new CategoryDAO().get(cid);
                String name = rs.getString("name");

                bean.setId(id);
                bean.setCategory(category);
                bean.setName(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 获取cid该分类下所有的属性集合
     *
     * @param: cid
     * @return: List<Property>
     */
    public List<Property> list(int cid) {
        return list(cid, 0, Short.MAX_VALUE);
    }

    /**
     * 获取cid该分类下指定范围的属性集合
     *
     * @param: cid
     * @param: start
     * @param: count
     * @return: List<Property>
     */
    public List<Property> list(int cid, int start, int count) {
        List<Property> beans = new ArrayList<>();

        String sql = "SELECT * FROM property WHERE cid = ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, cid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Property bean = new Property();
                int id = rs.getInt(1);
                Category category = new CategoryDAO().get(cid);
                String name = rs.getString("name");

                bean.setId(id);
                bean.setCategory(category);
                bean.setName(name);
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
