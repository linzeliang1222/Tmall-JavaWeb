package tmall.comparator;

import tmall.bean.Product;

import java.util.Comparator;

/**
 * @Description: 产品创建日期比较器
 * @Author: LinZeLiang
 * @Date: 2021-01-22
 */
public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        // 降序，日期晚的排在前面
        return o2.getCreateDate().compareTo(o1.getCreateDate());
    }
}
