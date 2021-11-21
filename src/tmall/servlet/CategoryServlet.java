package tmall.servlet;

import tmall.bean.Category;
import tmall.util.ImageUtil;
import tmall.util.Page;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryServlet extends BaseBackServlet {

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String, String> params = new HashMap<>();
        InputStream is = parseUpload(request, params);

        String name = params.get("name");
        Category c = new Category();
        c.setName(name);
        categoryDAO.add(c);

        // 服务器硬盘真实文件夹路径
        File imageFolder = new File(request.getSession().getServletContext().getRealPath("img/category"));
        // 文件夹路径加上图片名称及格式
        File file = new File(imageFolder, c.getId() + ".jpg");
        file.getParentFile().mkdirs();

        try {
            // available可以知道数据流里面有多少个字节可以读取，如果为0说明读取不了
            // 如果得到的流是空的或者可读取字节数位0，那么就不进行上传处理
            if (null != is && 0 != is.available()) {
                // 保存图片到服务器本地
                try (
                        FileOutputStream fos = new FileOutputStream(file)
                ) {
                    // 使用一个数组缓冲区，大小为1MB，即每次读取1MB
                    byte[] b = new byte[1024 * 1024];
                    int length = 0;
                    while (-1 != (length = is.read(b))) {
                        fos.write(b, 0, length);
                    }
                    // 清空缓冲区
                    fos.flush();
                    // 通过如下代码，把文件保存为jpg格式（而不是仅仅后缀名为jpg）
                    BufferedImage img = ImageUtil.change2jpg(file);
                    ImageIO.write(img, "jpg", file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "@admin_category_list";
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        categoryDAO.delete(id);
        // 删除对应的图片
        File imageFile = new File(request.getSession().getServletContext().getRealPath("img/category") + "/" + id + ".jpg");
        imageFile.delete();

        return "@admin_category_list";
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Category c = categoryDAO.get(id);
        // 将获得的要修改的分类存储到request域中，提供给editCategory页面
        request.setAttribute("c", c);
        return "admin/editCategory.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String, String> params = new HashMap<>();
        InputStream is = parseUpload(request, params);

        String name = params.get("name");
        int id = Integer.parseInt(params.get("id"));

        Category c = new Category();
        c.setId(id);
        c.setName(name);
        categoryDAO.update(c);

        File imageFolder = new File(request.getSession().getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, c.getId() + ".jpg");
        file.getParentFile().mkdirs();

        try {
            // 检查浏览器发送的数据是否收到了（有没有保存到内存中）
            if (null != is && 0 != is.available()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] b = new byte[1024 * 1024];
                    int length = 0;
                    while (-1 != (length = is.read(b))) {
                        fos.write(b, 0, length);
                    }
                    fos.flush();
                    // 通过如下代码，将文件保存为jpg格式
                    BufferedImage img = ImageUtil.change2jpg(file);
                    ImageIO.write(img, "jpg", file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "@admin_category_list";
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Category> cs = categoryDAO.list(page.getStart(), page.getCount());
        int total = categoryDAO.getTotal();
        page.setTotal(total);

        request.setAttribute("thecs", cs);
        request.setAttribute("page", page);

        return "admin/listCategory.jsp";
    }
}
