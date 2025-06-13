
package com.example.connector.aws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String DB_URL = "jdbc:postgresql://database-1.ct66kuqcw99y.us-east-2.rds.amazonaws.com:5432/postgres";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "!Sr19960309";

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