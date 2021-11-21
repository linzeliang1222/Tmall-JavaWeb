package tmall.bean;

import java.util.List;

/**
 * @Description: Category实体类
 * @Author: LinZeLiang
 * @Date: 2020-12-20
 */
public class Category {
    private int id;
    private String name;
    /**
     * 该分类下总的产品
     */
    List<Product> products;
    /**
     * 一个分类对应多行产品，一行产品又对应多个产品记录
     */
    List<List<Product>> productsByRow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category [name=" + name + "]";
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }

    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }
}
