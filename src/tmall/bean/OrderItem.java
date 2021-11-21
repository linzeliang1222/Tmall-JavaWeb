package tmall.bean;

/**
 * @Description: OrderItem实体类，其实就是购物车，订单项表示在购物车里的一个宝贝
 * @Author: LinZeLiang
 * @Date: 2020-12-20
 */
public class OrderItem {
    private int id;
    private Product product;
    private Order order;
    private User user;
    private int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
