package com.example.connector.netsuite;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.connector.dto.CustomerDto;
import com.example.connector.dto.CustomerItemDto;
import com.example.connector.dto.CustomerResponseDto;
import com.example.connector.repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NetsuiteCustomerClient {
    private CustomerRepository customerRepository;

    @Autowired
    public NetsuiteCustomerClient(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String getCustomerEmail(String accessToken, String customerId) throws Exception {
        String url = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/record/v1/customer/" + customerId
                + "?fields=email,entityId";

        System.out.println("token " + accessToken);
        System.out.println("request url " + url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken) // Fixed header name and added space
                .header("Accept", "application/json").header("Content-Type", "application/json").GET().build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        return body;
    }

    public List<CustomerItemDto> getCustomers(String accessToken) throws Exception {
        final String url = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/query/v1/suiteql";
        final String queryStr = """
                    SELECT
                    customer.id AS internalId,
                    customer.entityId AS custId,
                    customer.lastName AS lastname,
                    customer.firstName AS firstname,
                    customer.email AS email,
                    CustomerSubsidiaryRelationship.subsidiary AS subsidiary,
                    entityAddress.addrText AS address
                    FROM
                    customer
                    LEFT JOIN CustomerSubsidiaryRelationship ON Customer.ID = CustomerSubsidiaryRelationship.entity
                    AND CustomerSubsidiaryRelationship.isprimarysub = 'T'
                    LEFT JOIN entityAddressbook ON entityAddressbook.entity = customer.id
                    AND entityAddressbook.defaultbilling = 'T'
                    LEFT JOIN entityAddress ON entityAddress.nkey = entityAddressbook.AddressBookAddress
                    LEFT JOIN employee ON employee.id = customer.salesrep
                    WHERE
                    TO_DATE (customer.datecreated, 'MM-DD-YYYY') BETWEEN '06-13-2025' AND TO_DATE  (SYSDATE, 'MM-DD-YYYY')
                    ORDER BY
                    customer.datecreated DESC
                """;
        final String formmatedQuery = String.format("{\"q\": \"%s\"}", queryStr.replaceAll("\\s+", " ").trim());

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
                .header("Prefer", "transient").POST(HttpRequest.BodyPublishers.ofString(formmatedQuery)).build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponseDto parsedResponse = mapper.readValue(response.body(), CustomerResponseDto.class);
        List<CustomerItemDto> customers = parsedResponse.getItems();

        return customers;
    }

    public void createCustomers(String accessToken, List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        String url = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/record/v1/customer";
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        List<CustomerDto> updatedCustomers = new ArrayList<>();

        for (CustomerDto customer : customers) {
            if (customer.getInternalId() != 0)
                continue;

            try {
                String requestBody = mapper.writeValueAsString(customer);
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                if (statusCode >= 200 && statusCode <= 300) {
                    System.out.println("Customer " + customer.getFirstname() + " " + customer.getLastname()
                            + " created succesfully.");

                    String location = response.headers().firstValue("location").orElse(null);

                    System.out.println("location: " + location);

                    if (location != null) {
                        System.out.println("Location header: " + location);
                        String[] parts = location.split("/");
                        String internalId = parts[parts.length - 1];

                        System.out.println("internalid: " + internalId);

                        customer.setInternalId(Integer.parseInt(internalId));
                        updatedCustomers.add(customer);
                    }
                } else {
                    // System.err.println("Failed to create customer " + customer.getFirstname() + "
                    // "
                    // + customer.getLastname() + ". Status: " + statusCode);
                    String responseBody = response.body();
                    try {
                        ObjectMapper errorMapper = new ObjectMapper();
                        JsonNode root = errorMapper.readTree(responseBody);

                        if (root.has("o:errorDetails")) {
                            for (JsonNode detail : root.get("o:errorDetails")) {
                                String msg = detail.has("detail") ? detail.get("detail").asText() : "";
                                String code = detail.has("o:errorCode") ? detail.get("o:errorCode").asText() : "";
                                String path = detail.has("o:errorPath") ? detail.get("o:errorPath").asText() : "";
                                System.err.println("Detail: " + msg);
                                System.err.println("Error code: " + code);

                                if (!path.isEmpty()) {
                                    System.err.println("Error Path: " + path);
                                }
                            }
                        }
                    } catch (Exception parseEx) {
                        System.err.println("Failed to parse error details: " + parseEx.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error creating customer " + customer.getFirstname() + " " + customer.getLastname()
                        + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (!updatedCustomers.isEmpty()) {
            System.out.println(updatedCustomers);
            customerRepository.updateCustomersById(updatedCustomers);
        }
    }

    public void updateCustomers(String accessToken, List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        String baseUrl = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/record/v1/customer/";
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        for (CustomerDto customer : customers) {
            System.out.println(customer);
            try {
                if (customer.getInternalId() == null) {
                    System.err.println("Customer internal id is required for update.");
                    continue;
                }

                String url = baseUrl + customer.getInternalId();
                String requestBody = mapper.writeValueAsString(customer);

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody)).build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                System.out.println("PATCH payload: " + requestBody);
                System.out.println("Status: " + statusCode);

                // if (statusCode < 200 || statusCode >= 300) {
                // // Parse and print error details
                // String responseBody = response.body();
                // System.err.println("Error response: " + responseBody);
                // try {
                // ObjectMapper errorMapper = new ObjectMapper();
                // com.fasterxml.jackson.databind.JsonNode root =
                // errorMapper.readTree(responseBody);
                // if (root.has("o:errorDetails")) {
                // for (com.fasterxml.jackson.databind.JsonNode detail :
                // root.get("o:errorDetails")) {
                // String msg = detail.has("detail") ? detail.get("detail").asText() : "";
                // String code = detail.has("o:errorCode") ? detail.get("o:errorCode").asText()
                // : "";
                // String path = detail.has("o:errorPath") ? detail.get("o:errorPath").asText()
                // : "";
                // System.err.println("Detail: " + msg);
                // System.err.println("Error Code: " + code);
                // if (!path.isEmpty()) {
                // System.err.println("Error Path: " + path);
                // }
                // }
                // }
                // } catch (Exception parseEx) {
                // System.err.println("Failed to parse error details: " + parseEx.getMessage());
                // }
                // } else {
                // System.out.println("resposne: " + response.body());
                // }
            } catch (Exception e) {
                // TODO: handle exception
                System.err.println("Error creating customer");
                e.printStackTrace();
            }
        }
    }

    public void deleteCustomers(String accessToken, List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        String baseUrl = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/record/v1/customer/";
        HttpClient client = HttpClient.newHttpClient();

        for (CustomerDto customer : customers) {
            if (customer.getInternalId() == null) {
                System.err.println("Customer internal id is required for delete.");
                continue;
            }

            String url = baseUrl + customer.getInternalId();

            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
                        .DELETE().build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    System.out.println("Deleted customer with internal id=" + customer.getInternalId());
                } else {
                    System.err.println("Failed to delete customer with internal id=" + customer.getInternalId()
                            + ". Status: " + statusCode + ". Response: " + response.body());
                }

            } catch (Exception e) {
                System.err.println(
                        "Error deleting customer with internal id=" + customer.getInternalId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

// TERM"},"unbilledOrders":0.0}