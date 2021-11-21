package tmall.comparator;

import tmall.bean.Product;

import java.util.Comparator;

/**
 * @Description: 产品价格比较器
 * @Author: LinZeLiang
 * @Date: 2021-01-22
 */
public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        // 升序，价格低的在前面
        if (o1.getPromotePrice() - o2.getPromotePrice() > 0) {
            return 1;
        } else if (o1.getPromotePrice() - o2.getPromotePrice() < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
