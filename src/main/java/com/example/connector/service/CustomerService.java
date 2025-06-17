package com.example.connector.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.connector.aws.DbConnection;
import com.example.connector.dto.CustomerResponseDto;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerService {
    // public void getCustomers() throws SQLException {
    // try {
    // Connection connection = DbConnection.getConnection();
    // System.out.println("Database connection successful!");
    // } catch (SQLException e) {
    // System.out.println("Database connection failed: " + e.getMessage());
    // }
    // }

    public void syncCustomers(String accessToken, NetsuiteCustomerClient netsuiteCustomerClient) throws Exception {
        String customerJson = netsuiteCustomerClient.getCustomers(accessToken);
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponseDto response = mapper.readValue(customerJson, CustomerResponseDto.class);

        List<CustomerResponseDto.CustomerItem> customers = response.getItems();
        if (customers == null)
            return;

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO customers (cust_id, email, firstname, lastname) VALUES (?, ?, ?, ?) ";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (CustomerResponseDto.CustomerItem customer : customers) {
                stmt.setString(1, customer.getCust_id());
                stmt.setString(2, customer.getEmail());
                stmt.setString(3, customer.getFirstname());
                stmt.setString(4, customer.getLastname());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

}
