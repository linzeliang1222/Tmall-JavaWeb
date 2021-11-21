package tmall.comparator;

import tmall.bean.Product;

import java.util.Comparator;

/**
 * @Description: 产品销量比较器
 * @Author: LinZeLiang
 * @Date: 2021-01-22
 */
public class ProductSaleCountComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        // 降序，销量高的放在前面
        if (o2.getSaleCount() - o1.getSaleCount() > 0) {
            return 1;
        } else if (o2.getSaleCount() - o1.getSaleCount() < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
