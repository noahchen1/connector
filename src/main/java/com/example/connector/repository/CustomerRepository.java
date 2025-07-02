package com.example.connector.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.connector.aws.DbConnection;
import com.example.connector.dto.CustomerDto;

@Repository
public class CustomerRepository {
    public void printAllCustomers() throws Exception {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("cust_id") + ", " + rs.getString("email") + ", "
                        + rs.getString("firstname") + ", " + rs.getString("lastname"));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public List<CustomerDto> getAllCustomers() throws Exception {
        List<CustomerDto> customers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql =
                    "SELECT internal_id, cust_id, email, firstname, lastname, subsidiary, address, id FROM customers";
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
                customer.setId(rs.getInt("id"));
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

        int[] results = null;
        try (Connection conn = DbConnection.getConnection()) {
            final String sql =
                    "INSERT INTO customers (internal_id, cust_id, email, firstname, lastname, subsidiary, address) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            int idx = 0;
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

            results = stmt.executeBatch();

            for (CustomerDto customer : customers) {
                int result = results != null && idx < results.length ? results[idx] : 0;

                if (result >= 0) {
                    System.out.println("Created customer: custId=" + customer.getCustId()
                            + ", firstname=" + customer.getFirstname() + ", lastname="
                            + customer.getLastname());
                }
                idx++;
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println("Failed to add customers to DB: " + e.getMessage());
        }
    }

    public void updateCustomers(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        int[] results = null;
        int idx = 0;
        try (Connection conn = DbConnection.getConnection()) {
            final String sql =
                    "UPDATE customers SET cust_id = ?, email = ?, firstname = ?, lastname = ?, subsidiary = ?, address = ? WHERE internal_id = ?";
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

            results = stmt.executeBatch();
            idx = 0;
            for (CustomerDto customer : customers) {
                int result = results != null && idx < results.length ? results[idx] : 0;
                if (result >= 0) {
                    System.out.println("Updated customer: custId=" + customer.getCustId()
                            + ", firstname=" + customer.getFirstname() + ", lastname="
                            + customer.getLastname());
                }
                idx++;
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println("Failed to update customers in DB: " + e.getMessage());
        }
    }

    public void updateCustomersById(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        int[] results = null;
        int idx = 0;

        try (Connection conn = DbConnection.getConnection()) {
            final String sql =
                    "UPDATE customers SET internal_id = ?, cust_id = ?, email = ?, firstname = ?, lastname = ?, subsidiary = ?, address = ? WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            for (CustomerDto customer : customers) {
                stmt.setInt(1, customer.getInternalId());
                stmt.setString(2, customer.getCustId());
                stmt.setString(3, customer.getEmail());
                stmt.setString(4, customer.getFirstname());
                stmt.setString(5, customer.getLastname());
                stmt.setInt(6, customer.getSubsidiary());
                stmt.setString(7, customer.getAddress());
                stmt.setInt(8, customer.getId());
                stmt.addBatch();
            }

            results = stmt.executeBatch();
            idx = 0;
            for (CustomerDto customer : customers) {
                int result = results != null && idx < results.length ? results[idx] : 0;
                if (result >= 0) {
                    System.out.println("Updated customer by id: id=" + customer.getId()
                            + ", internalId=" + customer.getInternalId() + ", custId="
                            + customer.getCustId());
                }
                idx++;
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println("Failed to update customers by row id: " + e.getMessage());
        }
    }

    public void deleteCustomers(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        int[] results;
        int idx = 0;

        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "DELETE FROM customers WHERE internal_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            for (CustomerDto customer : customers) {
                stmt.setInt(1, customer.getInternalId());
                stmt.addBatch();
            }

            results = stmt.executeBatch();

            for (CustomerDto customer : customers) {
                int result = results != null && idx < results.length ? results[idx] : 0;

                if (result >= 0) {
                    System.out.println("Deleted customer with internal id="
                            + customer.getInternalId() + ", custId=" + customer.getCustId() + " "
                            + customer.getFirstname() + " " + customer.getLastname());
                }

                idx++;
            }

        } catch (Exception e) {
            System.err.println("Failed to delete customers in DB: " + e.getMessage());
        }
    }
}
