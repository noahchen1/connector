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
    // public void updateCustomers(List<CustomerResponseDto.CustomerItem> customers)
    // throws Exception {
    // try (Connection conn = DbConnection.getConnection()) {
    // final String selectSql = "SELECT email, firstname, lastname FROM customers
    // WHERE cust_id = ?";
    // final String insertSql = "INSERT INTO customers (cust_id, email, firstname,
    // lastname) VALUES (?, ?, ?, ?) ";
    // final String updateSql = "UPDATE customers SET email = ?, firstname = ?,
    // lastname = ? WHERE cust_id = ?";

    // for (CustomerResponseDto.CustomerItem customer : customers) {
    // PreparedStatement selectStmt = conn.prepareStatement(selectSql);
    // selectStmt.setString(1, customer.getCust_id());
    // ResultSet rs = selectStmt.executeQuery();

    // if (rs.next()) {
    // boolean needsUpdate = false;

    // if (!Objects.equals(rs.getString("email"), customer.getEmail()))
    // needsUpdate = true;
    // if (!Objects.equals(rs.getString("firstname"), customer.getFirstname()))
    // needsUpdate = true;
    // if (!Objects.equals(rs.getString("lastname"), customer.getLastname()))
    // needsUpdate = true;

    // if (needsUpdate) {
    // PreparedStatement updateStmt = conn.prepareStatement(updateSql);
    // updateStmt.setString(1, customer.getEmail());
    // updateStmt.setString(2, customer.getFirstname());
    // updateStmt.setString(3, customer.getLastname());
    // updateStmt.setString(4, customer.getCust_id());
    // updateStmt.executeUpdate();
    // updateStmt.close();
    // }

    // } else {
    // PreparedStatement insertStmt = conn.prepareStatement(insertSql);
    // insertStmt.setString(1, customer.getCust_id());
    // insertStmt.setString(2, customer.getEmail());
    // insertStmt.setString(3, customer.getFirstname());
    // insertStmt.setString(4, customer.getLastname());
    // insertStmt.executeUpdate();
    // insertStmt.close();
    // }

    // selectStmt.close();
    // }

    // } catch (Exception e) {
    // System.err.println("Error: " + e.getMessage());
    // }
    // }

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
            String sql = "SELECT internal_id, cust_id, email, firstname, lastname, subsidiary, address FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerDto customer = new CustomerDto();
                customer.setInternalId(rs.getInt("internal_id"));
                customer.setCustId(rs.getString("cust_id"));
                customer.setEmail(rs.getString("email"));
                customer.setFirstname(rs.getString("firstname"));
                customer.setLastname(rs.getString("lastname"));
                customer.setSubsidiary(rs.getInt("subsidiary"));
                customer.setAddress(rs.getString("address"));
                customers.add(customer);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return customers;
    }

    public void insertCustomers(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "INSERT INTO customers (internal_id, cust_id, email, firstname, lastname, subsidiary, address) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            for (CustomerDto customer : customers) {
                stmt.setInt(1, customer.getInternalId());
                stmt.setString(2, customer.getCustId());
                stmt.setString(3, customer.getEmail());
                stmt.setString(4, customer.getFirstname());
                stmt.setString(5, customer.getLastname());
                stmt.setInt(6, customer.getSubsidiary());
                stmt.setString(7, customer.getAddress());
                stmt.addBatch();
            }

            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void updateCustomers(List<CustomerDto> customers) {
        System.out.println("customer to update: " + customers);
        if (customers == null || customers.isEmpty())
            return;
        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "UPDATE customers SET cust_id = ?, email = ?, firstname = ?, lastname = ?, subsidiary = ?, address = ? WHERE internal_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (CustomerDto customer : customers) {
                stmt.setString(1, customer.getCustId());
                stmt.setString(2, customer.getEmail());
                stmt.setString(3, customer.getFirstname());
                stmt.setString(4, customer.getLastname());
                stmt.setInt(5, customer.getSubsidiary());
                stmt.setString(6, customer.getAddress());
                stmt.setInt(7, customer.getInternalId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
