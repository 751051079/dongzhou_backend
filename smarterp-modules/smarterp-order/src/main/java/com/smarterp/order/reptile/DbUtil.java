package com.smarterp.order.reptile;
/**
 * 数据库连接的实现
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    private String url = "jdbc:mysql://localhost:3306/mercado_product_info?useUnicode=true&characterEncoding=utf8";
    private String dbUser = "root";
    private String dbPassword = "admin";
    private String dbDriver = "com.mysql.cj.jdbc.Driver";
    private Connection connection = null;
    public Connection getConnection() {
        try {
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            System.out.println("数据库连接成功！");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("数据库连接关闭");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

