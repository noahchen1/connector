package com.example.connector.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.connector.util.CustomerLogHelper;
import org.springframework.stereotype.Repository;
import com.example.connector.aws.DbConnection;
import com.example.connector.dto.CustomerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public class CustomerRepository {
    private final Logger logger =
            LoggerFactory.getLogger(CustomerRepository.class);

    public List<CustomerDto> getAllCustomers() {
        List<CustomerDto> customers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT internal_id, cust_id, email, firstname, lastname, subsidiary, address, id FROM customers";
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

            CustomerLogHelper.logCustomerAction(
                    logger,
                    "",
                    "",
                    "",
                    CustomerLogHelper.ActionType.FETCH_CUSTOMERS,
                    CustomerLogHelper.SourceType.DB,
                    CustomerLogHelper.Status.SUCCESS,
                    "",
                    false
            );
        } catch (Exception e) {
            CustomerLogHelper.logCustomerAction(
                    logger,
                    "",
                    "",
                    "",
                    CustomerLogHelper.ActionType.FETCH_CUSTOMERS,
                    CustomerLogHelper.SourceType.DB,
                    CustomerLogHelper.Status.FAIL,
                    e.getMessage(),
                    true
            );

            throw new RuntimeException(e);
        }

        return customers;
    }

    public void insertCustomers(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "INSERT INTO customers (internal_id, cust_id, email, firstname, lastname, subsidiary, address) VALUES (?, ?, ?, ?, ?, ?, ?)";

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

            int[] results = stmt.executeBatch();
            for (int i = 0; i < results.length; i++) {
                if (results[i] >= 0) {
                    CustomerDto customer = customers.get(i);

                    CustomerLogHelper.logCustomerAction(
                            logger,
                            customer.getCustId(),
                            customer.getFirstname(),
                            customer.getLastname(),
                            CustomerLogHelper.ActionType.CREATE_CUSTOMERS,
                            CustomerLogHelper.SourceType.DB,
                            CustomerLogHelper.Status.SUCCESS,
                            "",
                            false
                    );
                }
            }

            stmt.close();

        } catch (Exception e) {
            CustomerLogHelper.logCustomerAction(
                    logger,
                    "",
                    "",
                    "",
                    CustomerLogHelper.ActionType.CREATE_CUSTOMERS,
                    CustomerLogHelper.SourceType.DB,
                    CustomerLogHelper.Status.FAIL,
                    e.getMessage(),
                    true
            );

            throw new RuntimeException(e);
        }
    }

    public void updateCustomers(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        int[] results = null;
        int idx = 0;
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

            results = stmt.executeBatch();
            for (int i = 0; i < results.length; i++) {
                if (results[i] >= 0) {
                    CustomerDto customer = customers.get(i);

                    CustomerLogHelper.logCustomerAction(
                            logger,
                            customer.getCustId(),
                            customer.getFirstname(),
                            customer.getLastname(),
                            CustomerLogHelper.ActionType.UPDATE_CUSTOMERS,
                            CustomerLogHelper.SourceType.DB,
                            CustomerLogHelper.Status.SUCCESS,
                            "",
                            false
                    );
                }
            }

            stmt.close();
        } catch (Exception e) {
            CustomerLogHelper.logCustomerAction(
                    logger,
                    "",
                    "",
                    "",
                    CustomerLogHelper.ActionType.UPDATE_CUSTOMERS,
                    CustomerLogHelper.SourceType.DB,
                    CustomerLogHelper.Status.FAIL,
                    e.getMessage(),
                    true
            );

            throw new RuntimeException(e);
        }
    }

    public void updateCustomersById(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "UPDATE customers SET internal_id = ?, cust_id = ?, email = ?, firstname = ?, lastname = ?, subsidiary = ?, address = ? WHERE id = ?";

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

            int[] results = stmt.executeBatch();
            for (int i = 0; i < results.length; i++) {
                if (results[i] >= 0) {
                    CustomerDto customer = customers.get(i);

                    CustomerLogHelper.logCustomerAction(
                            logger,
                            customer.getCustId(),
                            customer.getFirstname(),
                            customer.getLastname(),
                            CustomerLogHelper.ActionType.UPDATE_CUSTOMERS,
                            CustomerLogHelper.SourceType.DB,
                            CustomerLogHelper.Status.SUCCESS,
                            "",
                            false
                    );
                }
            }

            stmt.close();
        } catch (Exception e) {
            CustomerLogHelper.logCustomerAction(
                    logger,
                    "",
                    "",
                    "",
                    CustomerLogHelper.ActionType.UPDATE_CUSTOMERS,
                    CustomerLogHelper.SourceType.DB,
                    CustomerLogHelper.Status.FAIL,
                    e.getMessage(),
                    true
            );

            throw new RuntimeException(e);
        }
    }

    public void deleteCustomers(List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;


        try (Connection conn = DbConnection.getConnection()) {
            final String sql = "DELETE FROM customers WHERE internal_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            for (CustomerDto customer : customers) {
                stmt.setInt(1, customer.getInternalId());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();

            for (int i = 0; i < results.length; i++) {
                if (results[i] >= 0) {
                    CustomerDto customer = customers.get(i);

                    CustomerLogHelper.logCustomerAction(
                            logger,
                            customer.getCustId(),
                            customer.getFirstname(),
                            customer.getLastname(),
                            CustomerLogHelper.ActionType.DELETE_CUSTOMERS,
                            CustomerLogHelper.SourceType.DB,
                            CustomerLogHelper.Status.SUCCESS,
                            "",
                            false
                    );
                }
            }

        } catch (Exception e) {
            CustomerLogHelper.logCustomerAction(
                    logger,
                    "",
                    "",
                    "db",
                    CustomerLogHelper.ActionType.DELETE_CUSTOMERS,
                    CustomerLogHelper.SourceType.DB,
                    CustomerLogHelper.Status.FAIL,
                    e.getMessage(),
                    true)
            ;

            throw new RuntimeException(e);
        }
    }
}
