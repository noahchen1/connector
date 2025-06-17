
package com.example.connector.aws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.connector.config.DbConfig;

public class DbConnection {
    private static final String DB_URL = DbConfig.getDbHost();
    private static final String DB_USERNAME = DbConfig.getDbUser();
    private static final String DB_PASSWORD = DbConfig.getDbPassword();

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Connection failed. Full error details:");
            e.printStackTrace();
            throw e;
        }
    }
}