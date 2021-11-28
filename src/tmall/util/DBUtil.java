package tmall.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Description: 数据库连接工具类
 * @Author: LinZeLiang
 * @Date: 2020-12-21
 */
public class DBUtil {
    static String ip = "127.0.0.1";
    static int port = 3306;
    static String database = "tmall";
    static String encoding = "UTF-8";
    static String loginName = "root";
    static String password = "root";

    static {
        try {
            // 初始化驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     *
     * @return: Connection
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s?characterEncoding=%s", ip, port, database, encoding);
        return DriverManager.getConnection(url, loginName, password);
    }
}
