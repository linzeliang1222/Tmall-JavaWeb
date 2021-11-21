package tmall.comparator;

import tmall.bean.Product;

import java.util.Comparator;

/**
 * @Description: 产品人气(评论)比较器
 * @Author: LinZeLiang
 * @Date: 2021-01-22
 */
public class ProductReviewComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        // 降序，评论数量多的在前面
        if (o2.getReviewCount() - o1.getReviewCount() > 0) {
            return 1;
        } else if (o2.getReviewCount() - o1.getReviewCount() < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
