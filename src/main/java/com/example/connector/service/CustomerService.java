package com.example.connector.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.example.connector.aws.DbConnection;

@Service
public class CustomerService {
    public void getCustomers() throws SQLException {
        try {
            Connection connection = DbConnection.getConnection();
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

}