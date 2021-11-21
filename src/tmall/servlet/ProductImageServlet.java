package tmall.servlet;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.dao.ProductImageDAO;
import tmall.util.ImageUtil;
import tmall.util.Page;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductImageServlet extends BaseBackServlet {
    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {// 上传文件的输入流
        InputStream is = null;
        // 提交上传文件时的参数
        Map<String, String> params = new HashMap<>();

        // 解析上传
        is = parseUpload(request, params);

        // 根据上传的参数生成productImage对象
        String type = params.get("type");
        int pid = Integer.parseInt(params.get("pid"));
        Product p = productDAO.get(pid);

        ProductImage pi = new ProductImage();
        pi.setType(type);
        pi.setProduct(p);
        productImageDAO.add(pi);

        // 生成文件和对应的文件夹
        String fileName = pi.getId() + ".jpg";
        String imageFolder = null;
        String imageFolder_small = null;
        String imageFolder_middle = null;
        if (ProductImageDAO.type_single.equals(pi.getType())) {
            imageFolder = request.getSession().getServletContext().getRealPath("img/productSingle");
            imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");
        } else {
            imageFolder = request.getSession().getServletContext().getRealPath("img/productDetail");
        }
        File file = new File(imageFolder, fileName);
        file.getParentFile().mkdirs();

        // 复制文件
        try {
            if (null != is && 0 != is.available()) {
                try (
                        FileOutputStream fos = new FileOutputStream(file)
                        ) {
                    byte[] b = new byte[1024 * 1024];
                    int length = 0;
                    while ((length = is.read(b)) != -1) {
                        fos.write(b, 0, length);
                    }
                    fos.flush();
                    // 将文件保存为jpg格式
                    BufferedImage img = ImageUtil.change2jpg(file);
                    ImageIO.write(img, "jpg", file);

                    if (ProductImageDAO.type_single.equals(pi.getType())) {
                        File file_small = new File(imageFolder_small, fileName);
                        File file_middle = new File(imageFolder_middle, fileName);

                        file_small.getParentFile().mkdirs();
                        file_middle.getParentFile().mkdirs();

                        ImageUtil.resizeImage(file, 56, 56, file_small);
                        ImageUtil.resizeImage(file, 217, 190, file_middle);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "@admin_productImage_list?pid=" + p.getId();
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        ProductImage pi = productImageDAO.get(id);
        productImageDAO.delete(id);

        if (ProductImageDAO.type_single.equals(pi.getType())) {
            String imageFolder_single = request.getSession().getServletContext().getRealPath("img/productSingle");
            String imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");
            // 删除
            File file_single = new File(imageFolder_single, pi.getId() + ".jpg");
            file_single.delete();
            File file_small = new File(imageFolder_small, pi.getId() + ".jpg");
            file_small.delete();
            File file_middle = new File(imageFolder_middle, pi.getId() + ".jpg");
            file_middle.delete();
        } else {
            String imageFolder_detail = request.getSession().getServletContext().getRealPath("img/productDetail");
            File file_detail = new File(imageFolder_detail, pi.getId() + ".jpg");
            file_detail.delete();
        }

        return "@admin_productImage_list?pid=" + pi.getProduct().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        // nowhere
        return null;
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        // nowhere
        return null;
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);
        List<ProductImage> pisSingle = productImageDAO.list(p, ProductImageDAO.type_single);
        List<ProductImage> pisDetail = productImageDAO.list(p, ProductImageDAO.type_detail);

        // 添加到request域中
        request.setAttribute("p", p);
        request.setAttribute("pisSingle", pisSingle);
        request.setAttribute("pisDetail", pisDetail);

        return "admin/listProductImage.jsp";
    }
}
