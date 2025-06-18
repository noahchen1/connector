package com.example.connector.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.example.connector.aws.DbConnection;
import com.example.connector.dto.CustomerResponseDto;

public class CustomerRepository {
    public void saveCustomers(List<CustomerResponseDto.CustomerItem> customers) throws Exception {
        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "INSERT INTO customers (cust_id, email, firstname, lastname) VALUES (?, ?, ?, ?) ";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (CustomerResponseDto.CustomerItem customer : customers) {
                stmt.setString(1, customer.getCust_id());
                stmt.setString(2, customer.getEmail());
                stmt.setString(3, customer.getFirstname());
                stmt.setString(4, customer.getLastname());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void printAllCustomers() throws Exception {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("cust_id") + ", " +
                        rs.getString("email") + ", " +
                        rs.getString("firstname") + ", " +
                        rs.getString("lastname"));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
