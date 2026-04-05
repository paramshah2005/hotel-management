package com.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://ep-quiet-glitter-a1oielwg-pooler.ap-southeast-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_IM8Vy5DhedkU&sslmode=require&channelBinding=require";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Connected to database!");
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}