package com.example.connector.netsuite;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

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
}