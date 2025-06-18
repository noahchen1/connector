package com.example.connector.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.connector.dto.CustomerResponseDto;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.example.connector.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository = new CustomerRepository();

    public void syncCustomers(String accessToken, NetsuiteCustomerClient netsuiteCustomerClient) throws Exception {
        String customerJson = netsuiteCustomerClient.getCustomers(accessToken);
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponseDto response = mapper.readValue(customerJson, CustomerResponseDto.class);
        List<CustomerResponseDto.CustomerItem> customers = response.getItems();

        if (customers != null) {
            customerRepository.updateCustomers(customers);
            System.out.println("Customers synced to DB.");

            customerRepository.printAllCustomers();
        }
    }

}
