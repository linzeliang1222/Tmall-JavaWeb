package tmall.servlet;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.PropertyValue;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public class ProductServlet extends BaseBackServlet {
    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);

        String name = request.getParameter("name");
        String subTitle = request.getParameter("subTitle");
        float originalPrice = Float.parseFloat(request.getParameter("originalPrice"));
        float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        Product p = new Product();
        p.setName(name);
        p.setSubTitle(subTitle);
        p.setOriginalPrice(originalPrice);
        p.setPromotePrice(promotePrice);
        p.setStock(stock);
        p.setCategory(c);
        p.setCreateDate(new Date());

        productDAO.add(p);

        return "@admin_product_list?cid=" + cid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product p = productDAO.get(id);

        productDAO.delete(id);

        return "@admin_product_list?cid=" + p.getCategory().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product p = productDAO.get(id);

        request.setAttribute("p", p);

        return "admin/editProduct.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        int id = Integer.parseInt(request.getParameter("id"));

        Category c = categoryDAO.get(cid);
        String name = request.getParameter("name");
        String subTile = request.getParameter("subTitle");
        float originalPrice = Float.parseFloat(request.getParameter("originalPrice"));
        float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setSubTitle(subTile);
        p.setOriginalPrice(originalPrice);
        p.setPromotePrice(promotePrice);
        p.setStock(stock);
        p.setCategory(c);

        // 不用管图片地址，因为获取product的时候，就会自动调用serFirstProductImage方法为product设置图片
        productDAO.update(p);

        return "@admin_product_list?cid=" + cid;
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);

        List<Product> ps = productDAO.list(cid, page.getStart(), page.getCount());
        int total = propertyDAO.getTotal(cid);
        page.setTotal(total);
        page.setParam("&cid=" + c.getId());

        request.setAttribute("ps", ps);
        request.setAttribute("c", c);
        request.setAttribute("page", page);

        return "admin/listProduct.jsp";
    }

    /**
     * 查询产品信息，转发到editPropertyValue页面中去
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String editPropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product p = productDAO.get(id);
        propertyValueDAO.init(p);
        List<PropertyValue> pvs = propertyValueDAO.list(p.getId());

        request.setAttribute("p", p);
        request.setAttribute("pvs", pvs);

        return "admin/editProductValue.jsp";
    }

    /**
     * 将编辑的提交到数据库中去
     *
     * @param: request
     * @param: response
     * @param: page
     * @return: String
     */
    public String updatePropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pvid = Integer.parseInt(request.getParameter("pvid"));
        String value = request.getParameter("value");

        PropertyValue pv = propertyValueDAO.get(pvid);
        pv.setValue(value);
        propertyValueDAO.update(pv);

        return "%success";
    }
}
