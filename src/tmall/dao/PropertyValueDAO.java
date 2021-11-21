package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Property;
import tmall.bean.PropertyValue;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: PropertyValue对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-23
 */
public class PropertyValueDAO {

    /**
     * 获取所有属性值的个数
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;

        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()
                ) {

            String sql = "SELECT COUNT(*) FROM propertyvalue";

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
     * 添加属性值，同时将其和Property、Product关联起来
     *
     * @param: bean
     */
    public void add (PropertyValue bean) {
        String sql = "INSERT INTO propertyvalue VALUES (NULL, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            pstmt.setInt(1, bean.getProduct().getId());
            pstmt.setInt(2, bean.getProperty().getId());
            pstmt.setString(3, bean.getValue());
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
     * 更新属性值
     *
     * @param: bean
     */
    public void update(PropertyValue bean) {
        String sql = "UPDATE propertyvalue SET pid = ?, ptid = ?, value = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, bean.getProduct().getId());
            pstmt.setInt(2, bean.getProperty().getId());
            pstmt.setString(3, bean.getValue());
            pstmt.setInt(4, bean.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除属性值
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM propertyvalue WHERE id = ?";

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
     * 通过id获取属性值
     *
     * @param: id
     * @return: PropertyValue
     */
    public PropertyValue get(int id) {
        PropertyValue bean = null;

        String sql = "SELECT * FROM propertyvalue WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new PropertyValue();
                int pid = rs.getInt("pid");
                int ptid = rs.getInt("ptid");
                Product product = new ProductDAO().get(pid);
                Property property = new PropertyDAO().get(ptid);
                String value = rs.getString("value");

                bean.setId(id);
                bean.setProduct(product);
                bean.setProperty(property);
                bean.setValue(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 通过pid和ptid获取属性值
     *
     * @param: ptid
     * @param: pid
     * @return: PropertyValue
     */
    public PropertyValue get(int ptid, int pid) {
        PropertyValue bean = null;

        String sql = "SELECT * FROM propertyvalue WHERE ptid = ? AND pid = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, ptid);
            pstmt.setInt(2, pid);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new PropertyValue();
                int id = rs.getInt(1);
                Product product = new ProductDAO().get(pid);
                Property property = new PropertyDAO().get(ptid);
                String value = rs.getString("value");

                bean.setId(id);
                bean.setProduct(product);
                bean.setProperty(property);
                bean.setValue(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 获取所有的属性值集合
     *
     * @return: List<PropertyValue>
     */
    public List<PropertyValue> list() {
        return list(0, Short.MAX_VALUE);
    }

    /**
     * 获取指定范围的属性值集合
     *
     * @param: start
     * @param: count
     * @return: List<PropertyValue>
     */
    public List<PropertyValue> list(int start, int count) {
        List<PropertyValue> beans = new ArrayList<>();

        String sql = "SELECT * FROM propertyvalue ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, start);
            pstmt.setInt(2, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PropertyValue bean = new PropertyValue();
                int id = rs.getInt(1);
                int pid = rs.getInt("pid");
                int ptid = rs.getInt("ptid");
                Product product = new ProductDAO().get(pid);
                Property property = new PropertyDAO().get(ptid);
                String value = rs.getString("value");

                bean.setId(id);
                bean.setProduct(product);
                bean.setProperty(property);
                bean.setValue(value);
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 初始化该产品的分类下的所有属性
     *
     * @param: p
     */
    public void init(Product p) {
        // 先获取p这个产品的所有属性集合
        List<Property> pts = new PropertyDAO().list(p.getCategory().getId());

        // 遍历每一个属性
        for (Property pt : pts) {
            // 然后通过Product和Property的id获取属性对应的属性值
            PropertyValue pv = this.get(pt.getId(), p.getId());
            // 如果属性值不存在，那么就需要创建一个新的属性值
            if (null == pv) {
                pv = new PropertyValue();
                pv.setProduct(p);
                pv.setProperty(pt);
                // pv.setValue();
                this.add(pv);
            }
        }
    }

    /**
     * 查询某个产品的所有属性值
     *
     * @param: pid
     * @return: List<PropertyValue>
     */
    public List<PropertyValue> list(int pid) {
        List<PropertyValue> beans = new ArrayList<>();

        String sql = "SELECT * FROM propertyvalue WHERE pid = ? ORDER BY ptid DESC";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, pid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PropertyValue bean = new PropertyValue();
                int id = rs.getInt(1);
                int ptid = rs.getInt("ptid");
                String value = rs.getString("value");
                Product product = new ProductDAO().get(pid);
                Property property = new PropertyDAO().get(ptid);

                bean.setId(id);
                bean.setProduct(product);
                bean.setProperty(property);
                bean.setValue(value);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
