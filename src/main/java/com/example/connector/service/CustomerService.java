package com.example.connector.service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.connector.dto.CustomerDto;
import com.example.connector.dto.CustomerItemDto;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.example.connector.repository.CustomerRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository = new CustomerRepository();

    private CustomerDto toCustomerDto(CustomerItemDto item) {
        CustomerDto customer = new CustomerDto();

        customer.setInternalId(item.getInternalId());
        customer.setCustId(item.getCustId());
        customer.setEmail(item.getEmail());
        customer.setFirstname(item.getFirstname());
        customer.setLastname(item.getLastname());
        customer.setSubsidiary(item.getSubsidiary());
        customer.setAddress(item.getAddress());

        return customer;
    }

    private void runUpdate(
            List<CustomerDto> sourceList,
            List<CustomerDto> targetList,
            Map<Integer, CustomerDto> targetMap,
            Consumer<List<CustomerDto>> createFn,
            Consumer<List<CustomerDto>> updateFn) {
        List<CustomerDto> toInsert = new java.util.ArrayList<>();
        List<CustomerDto> toUpdate = new java.util.ArrayList<>();

        for (CustomerDto source : sourceList) {
            CustomerDto target = targetMap.get(source.getInternalId());
            if (target == null) {
                toInsert.add(source);
            } else if (!source.equals(target)) {
                toUpdate.add(source);
            }
        }

        createFn.accept(toInsert);
        updateFn.accept(toUpdate);
    }

    public void syncCustomers(String accessToken, NetsuiteCustomerClient netsuiteCustomerClient) throws Exception {
        List<CustomerItemDto> nsCustomers = netsuiteCustomerClient.getCustomers(accessToken);
        List<CustomerDto> dbCustomers = customerRepository.getAllCustomers();

        List<CustomerDto> parsedNsCustomers = nsCustomers.stream()
                .map(this::toCustomerDto)
                .collect(Collectors.toList());

        Map<Integer, CustomerDto> nsMap = parsedNsCustomers.stream()
                .collect(Collectors.toMap(CustomerDto::getInternalId, c -> c));

        Map<Integer, CustomerDto> dbMap = dbCustomers.stream()
                .collect(Collectors.toMap(CustomerDto::getInternalId, c -> c));

        // Collect for batch processing
        runUpdate(parsedNsCustomers, dbCustomers, dbMap, toInsert -> customerRepository.insertCustomers(toInsert),
                toUpdate -> customerRepository.updateCustomers(toUpdate));

        runUpdate(dbCustomers, parsedNsCustomers, nsMap,
                toInsert -> netsuiteCustomerClient.createCustomers(accessToken, toInsert),
                toUpdate -> netsuiteCustomerClient.updateCustomers(accessToken, toUpdate));
    }

}