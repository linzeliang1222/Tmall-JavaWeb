package tmall.comparator;

import tmall.bean.Product;

import java.util.Comparator;

/**
 * @Description: 产品综合比较器
 * @Author: LinZeLiang
 * @Date: 2021-01-22
 */
public class ProductAllComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        // 按降序排序
        if (o2.getReviewCount() * o2.getSaleCount() - o1.getReviewCount() * o1.getSaleCount() > 0) {
            return 1;
        } else if (o2.getReviewCount() * o2.getSaleCount() - o1.getReviewCount() * o1.getSaleCount() < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
