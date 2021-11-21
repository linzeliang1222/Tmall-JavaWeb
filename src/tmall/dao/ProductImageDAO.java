package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: ProductImage对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-22
 */
public class ProductImageDAO {

    /**
     * 表示单个图片
     */
    public static final String type_single = "type_single";
    /**
     * 表示详情图片
     */
    public static final String type_detail = "type_detail";

    /**
     * 获取图片总数
     *
     * @return: int
     */
    public int getTotal() {
        int total = 0;

        try (
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()
                ) {

            String sql = "SELECT COUNT(*) FROM productimage";

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
     * 添加图片
     *
     * @param: bean
     */
    public void add(ProductImage bean) {
        String sql = "INSERT INTO productimage VALUES (NULL, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {

            pstmt.setInt(1, bean.getProduct().getId());
            pstmt.setString(2, bean.getType());
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
     * 不需要更新图片功能，如果要更新，就把原来的删了替换add新的即可
     *
     * @param: bean
     */
    public void update(ProductImage bean) {
    }

    /**
     * 根据id删除图片
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM productimage WHERE id = ?";

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
     * 根据id获取一个图片对象
     *
     * @param: id
     * @return: ProductImage
     */
    public ProductImage get(int id) {
        ProductImage bean = null;

        String sql = "SELECT * FROM productimage WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new ProductImage();
                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                String type = rs.getString("type");

                bean.setId(id);
                bean.setProduct(product);
                bean.setType(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 查询指定产品p的type类型的所有图片
     *
     * @param: p
     * @param: type
     * @return: List<ProductImage>
     */
    public List<ProductImage> list(Product p, String type) {
        return list(p, type, 0, Short.MAX_VALUE);
    }

    /**
     * 查询指定产品p的type类型的部分图片
     *
     * @param: p
     * @param: type
     * @param: start
     * @param: count
     * @return: List<ProductImage>
     */
    public List<ProductImage> list(Product p, String type, int start, int count) {
        List<ProductImage> beans = new ArrayList<>();

        String sql = "SELECT * FROM productimage WHERE pid = ? AND type = ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setInt(1, p.getId());
            pstmt.setString(2, type);
            pstmt.setInt(3, start);
            pstmt.setInt(4, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProductImage bean = new ProductImage();
                int id = rs.getInt(1);

                bean.setId(id);
                bean.setProduct(p);
                bean.setType(type);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
