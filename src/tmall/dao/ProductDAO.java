package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: Product对象的ORM映射
 * @Author: LinZeLiang
 * @Date: 2020-12-25
 */
public class ProductDAO {

    /**
     * 通过某分类下的产品总数
     *
     * @param: cid
     * @return: int
     */
    public int getTotal(int cid) {
        int total = 0;

        String sql = "SELECT COUNT(*) FROM product WHERE cid = ?";

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
     * 添加新的产品
     *
     * @param: bean
     */
    public void add(Product bean) {
        String sql = "INSERT INTO product VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getSubTitle());
            pstmt.setFloat(3, bean.getOriginalPrice());
            pstmt.setFloat(4, bean.getPromotePrice());
            pstmt.setInt(5, bean.getStock());
            pstmt.setInt(6, bean.getCategory().getId());
            pstmt.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));

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
     * 更新产品信息
     *
     * @param: bean
     */
    public void update(Product bean) {
        String sql = "UPDATE product SET name = ?, subTitle = ?, originalPrice = ?, promotePrice = ?, stock = ?, cid = ?, createDate = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getSubTitle());
            pstmt.setFloat(3, bean.getOriginalPrice());
            pstmt.setFloat(4, bean.getPromotePrice());
            pstmt.setInt(5, bean.getStock());
            pstmt.setInt(6, bean.getCategory().getId());
            pstmt.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));
            pstmt.setInt(8, bean.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过id将产品删除
     *
     * @param: id
     */
    public void delete(int id) {
        String sql = "DELETE FROM product WHERE id = ?";

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
     * 通过产品id获取产品
     *
     * @param: id
     * @return: Product
     */
    public Product get(int id) {
        Product bean = null;

        String sql = "SELECT * FROM product WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new Product();
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float originalPrice = rs.getFloat("originalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                int cid = rs.getInt("cid");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                Category category = new CategoryDAO().get(cid);

                bean.setId(id);
                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOriginalPrice(originalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCategory(category);
                bean.setCreateDate(createDate);
                setFirstProductImage(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 通过cid获取某分类下的所有产品集合
     *
     * @param: cid
     * @return: List<Product>
     */
    public List<Product> list(int cid) {
        return list(cid, 0, Short.MAX_VALUE);
    }

    /**
     * 通过cid获取某分类下的指定范围的产品集合
     *
     * @param: cid
     * @param: start
     * @param: count
     * @return: List<Product>
     */
    public List<Product> list(int cid, int start, int count) {
        List<Product> beans = new ArrayList<>();
        // 由于该cid分类下的产品的分类都是同一个对象，只需创建一个即可
        Category category = new CategoryDAO().get(cid);

        String sql = "SELECT * FROM product WHERE cid = ? ORDER BY id DESC LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, cid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product bean = new Product();
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float originalPrice = rs.getFloat("originalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(id);
                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOriginalPrice(originalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCategory(category);
                bean.setCreateDate(createDate);
                setFirstProductImage(bean);
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 获取所有产品集合
     *
     * @return: List<Product>
     */
    public List<Product> list() {
        return list(0, Short.MAX_VALUE);
    }

    /**
     * 获取指定的部分范围产品集合
     *
     * @param: start
     * @param: count
     * @return: List<Product>
     */
    public List<Product> list(int start, int count) {
        List<Product> beans = new ArrayList<>();

        String sql = "SELECT * FROM product LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, start);
            pstmt.setInt(2, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product bean = new Product();
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float originalPrice = rs.getFloat("originalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                int cid = rs.getInt("cid");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                Category category = new CategoryDAO().get(cid);

                bean.setId(id);
                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOriginalPrice(originalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCategory(category);
                bean.setCreateDate(createDate);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }

    /**
     * 初始化传入的分类集合，将产品添加到属于它的分类下
     *
     * @param: cs
     */
    public void fill(List<Category> cs) {
        for (Category c : cs) {
            this.fill(c);
        }
    }

    /**
     * 将产品添加到分类c下
     *
     * @param: c
     */
    public void fill(Category c) {
        // 先获取该分类下的总产品集合
        List<Product> products = this.list(c.getId());
        // 然后直接加到c分类中即可
        c.setProducts(products);
    }

    /**
     * 对传入的集合的每个分类进行分行操作
     *
     * @param: cs
     */
    public void fillByRow(List<Category> cs) {
        // 每行有8个产品
        int productNumberEachRow = 8;

        for (Category c : cs) {
            // 先获取分类下总的
            List<Product> products = c.getProducts();
            // 存储分行后的结果
            List<List<Product>> productsByRow = new ArrayList<>();

            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsByEachRow = products.subList(i, size);
                productsByRow.add(productsByEachRow);
            }
            c.setProductsByRow(productsByRow);
        }
    }

    /**
     * 设置产品的第一张图片
     *
     * @param: p
     */
    public void setFirstProductImage(Product p) {
        // 获取所有单个图片
        List<ProductImage> pis = new ProductImageDAO().list(p, ProductImageDAO.type_single);
        if (!pis.isEmpty()) {
            // 有图片的情况下，将第一张作为默认的图片
            p.setFirstProductImage(pis.get(0));
        }
    }

    /**
     * 进行设置产品p的销售数量和评价数量
     *
     * @param: p
     */
    public void setSaleAndReviewNumber(Product p) {
        // 设置产品的销售数量
        int saleCount = new OrderItemDAO().getSaleCount(p.getId());
        p.setSaleCount(saleCount);

        // 设置产品的评价数量
        int reviewCount = new ReviewDAO().getCount(p.getId());
        p.setReviewCount(reviewCount);
    }

    /**
     * 设置许多个产品的销售数量和评价数量
     *
     * @param: products
     */
    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products) {
            setSaleAndReviewNumber(product);
        }
    }

    /**
     * 根据关键字查找产品
     *
     * @param: keyword 查询的关键字
     * @param: start
     * @param: count
     * @return: List<Product> 得到的结果就是查询后的结果
     */
    public List<Product> search(String keyword, int start, int count) {
        List<Product> beans = new ArrayList<>();

        String sql = "SELECT * FROM product WHERE name LIKE ? LIMIT ?, ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
                ) {

            pstmt.setString(1, "%" + keyword.trim() + "%");
            pstmt.setInt(2, start);
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product bean = new Product();
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float originalPrice = rs.getFloat("originalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                int cid = rs.getInt("cid");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                Category category = new CategoryDAO().get(cid);

                bean.setId(id);
                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOriginalPrice(originalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCategory(category);
                bean.setCreateDate(createDate);

                setFirstProductImage(bean);
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
