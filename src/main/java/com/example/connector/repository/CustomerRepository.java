package com.example.connector.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.connector.aws.DbConnection;
import com.example.connector.dto.CustomerDto;
import com.example.connector.dto.CustomerResponseDto;

public class CustomerRepository {
    public void updateCustomers(List<CustomerResponseDto.CustomerItem> customers) throws Exception {
        try (Connection conn = DbConnection.getConnection()) {
            final String selectSql = "SELECT email, firstname, lastname FROM customers WHERE cust_id = ?";
            final String insertSql = "INSERT INTO customers (cust_id, email, firstname, lastname) VALUES (?, ?, ?, ?) ";
            final String updateSql = "UPDATE customers SET email = ?, firstname = ?, lastname = ? WHERE cust_id = ?";

            for (CustomerResponseDto.CustomerItem customer : customers) {
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setString(1, customer.getCust_id());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    boolean needsUpdate = false;

                    if (!Objects.equals(rs.getString("email"), customer.getEmail()))
                        needsUpdate = true;
                    if (!Objects.equals(rs.getString("firstname"), customer.getFirstname()))
                        needsUpdate = true;
                    if (!Objects.equals(rs.getString("lastname"), customer.getLastname()))
                        needsUpdate = true;

                    if (needsUpdate) {
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setString(1, customer.getEmail());
                        updateStmt.setString(2, customer.getFirstname());
                        updateStmt.setString(3, customer.getLastname());
                        updateStmt.setString(4, customer.getCust_id());
                        updateStmt.executeUpdate();
                        updateStmt.close();
                    }

                } else {
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setString(1, customer.getCust_id());
                    insertStmt.setString(2, customer.getEmail());
                    insertStmt.setString(3, customer.getFirstname());
                    insertStmt.setString(4, customer.getLastname());
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }

                selectStmt.close();
            }

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


    public List<CustomerDto> getAllCustomers() throws Exception {
        List<CustomerDto> customers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT cust_id, email, firstname, lastname FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerDto customer = new CustomerDto();
                customer.setCustId(rs.getString("cust_id"));
                customer.setEmail(rs.getString("email"));
                customer.setFirstname(rs.getString("firstname"));
                customer.setLastname(rs.getString("lastname"));
                customers.add(customer);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return customers;
    }
}
