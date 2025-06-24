package com.example.connector.netsuite;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.connector.dto.CustomerDto;
import com.example.connector.dto.CustomerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NetsuiteCustomerClient {
    public String getCustomerEmail(String accessToken, String customerId) throws Exception {
        String url = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/record/v1/customer/"
                + customerId + "?fields=email,entityId";

        System.out.println("token " + accessToken);
        System.out.println("request url " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken) // Fixed header name and added space
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        return body;
    }

    public List<CustomerResponseDto.CustomerItem> getCustomers(String accessToken) throws Exception {
        final String query = """
                {
                    "q": "SELECT top(10) BUILTIN.DF(entity.id) AS cust_id, \
                    entity.firstname AS firstname, \
                    entity.lastname AS lastname, \
                    entity.email AS email \
                    FROM entity \
                    JOIN customer ON customer.id = entity.id \
                    WHERE EXTRACT(MONTH FROM TO_DATE(entity.datecreated, 'MM/DD/YYYY')) = EXTRACT(MONTH FROM CURRENT_DATE) \
                    AND EXTRACT(YEAR FROM TO_DATE(entity.datecreated, 'MM/DD/YYYY')) = EXTRACT(YEAR FROM CURRENT_DATE)"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/query/v1/suiteql"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .header("Prefer", "transient")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        CustomerResponseDto parsedResponse = mapper.readValue(response.body(), CustomerResponseDto.class);
        List<CustomerResponseDto.CustomerItem> customers = parsedResponse.getItems();

        return customers;
    }

    public void createCustomers(String accessToken, List<CustomerDto> customers) {
        if (customers == null || customers.isEmpty())
            return;

        System.out.println(customers);
        
        String url = "https://123456.suitetalk.api.netsuite.com/services/rest/record/v1/customer";
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        for (CustomerDto customer : customers) {
            try {
                String requestBody = mapper.writeValueAsString(customer);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response for customer " + customer.getCustId() + ": " + response.body());
            } catch (Exception e) {
                System.err.println("Error creating customer " + customer.getCustId() + ": " + e.getMessage());
            }
        }
    }

    public void updateCustomers(String accessToken, List<CustomerDto> customers) {

    }
}