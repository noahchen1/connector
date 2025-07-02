package com.example.connector.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        private void runUpdate(List<CustomerDto> sourceList, List<CustomerDto> targetList,
                        Map<Integer, CustomerDto> targetMap, Consumer<List<CustomerDto>> createFn,
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

        private List<CustomerDto> fetchNsCustomers(String accessToken,
                        NetsuiteCustomerClient client) throws Exception {
                return client.getCustomers(accessToken).stream().map(this::toCustomerDto)
                                .collect(Collectors.toList());
        }

        private void syncDbFromNs(List<CustomerDto> nsCustomers, List<CustomerDto> dbCustomers) {
                Map<Integer, CustomerDto> dbMap = dbCustomers.stream()
                                .collect(Collectors.toMap(CustomerDto::getInternalId, c -> c));

                runUpdate(nsCustomers, dbCustomers, dbMap,
                                toInsert -> customerRepository.insertCustomers(toInsert),
                                toUpdate -> customerRepository.updateCustomers(toUpdate));
        }

        private void syncNsFromDb(String accessToken, NetsuiteCustomerClient client,
                        List<CustomerDto> dbCustomers, List<CustomerDto> nsCustomers,
                        List<CustomerDto> createdInNs) {
                Map<Integer, CustomerDto> nsMap = nsCustomers.stream()
                                .collect(Collectors.toMap(CustomerDto::getInternalId, c -> c));

                runUpdate(dbCustomers, nsCustomers, nsMap, toInsert -> {
                        client.createCustomers(accessToken, toInsert);
                        createdInNs.addAll(toInsert);
                }, toUpdate -> {
                });
        }

        private void deleteDbCustomers(List<CustomerDto> dbCustomers,
                        List<CustomerDto> nsCustomers) {
                Map<Integer, CustomerDto> nsMap = nsCustomers.stream()
                                .collect(Collectors.toMap(CustomerDto::getInternalId, c -> c));
                List<CustomerDto> toDelete = dbCustomers.stream()
                                .filter(c -> nsMap.get(c.getInternalId()) == null)
                                .collect(Collectors.toList());

                if (!toDelete.isEmpty())
                        customerRepository.deleteCustomers(toDelete);
        }

        @Transactional
        public void syncCustomers(String accessToken, NetsuiteCustomerClient netsuiteCustomerClient)
                        throws Exception {
                List<CustomerDto> nsCustomers =
                                fetchNsCustomers(accessToken, netsuiteCustomerClient);
                List<CustomerDto> dbCustomers = customerRepository.getAllCustomers();
                List<CustomerDto> createdInNs = new ArrayList<>();

                try {
                        syncDbFromNs(nsCustomers, dbCustomers);
                        syncNsFromDb(accessToken, netsuiteCustomerClient, dbCustomers, nsCustomers,
                                        createdInNs);

                        List<CustomerDto> updatedNsCustomers =
                                        fetchNsCustomers(accessToken, netsuiteCustomerClient);
                        deleteDbCustomers(dbCustomers, updatedNsCustomers);
                } catch (Exception e) {
                        try {
                                netsuiteCustomerClient.deleteCustomers(accessToken, createdInNs);
                        } catch (Exception err) {
                                System.err.println(
                                                "Failed to revert changed to NS, manually intervention required: "
                                                                + e.getMessage());
                        }
                        throw e;
                }
        }

}
