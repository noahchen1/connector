package com.example.connector.netsuite;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.connector.config.NetsuiteConfig;
import com.example.connector.jwt.JwtService;
import com.example.connector.jwt.KeyLoader;

@Service
public class NetsuiteAuthClient {
    private final NetsuiteConfig config;
    private final JwtService jwtService;

    @Autowired
    public NetsuiteAuthClient(NetsuiteConfig config, JwtService jwtService) {
        this.config = config;
        this.jwtService = jwtService;
    }

    public String fetchAccessToken() throws Exception {
        ECPrivateKey privateKey = KeyLoader.loadEcPrivateKey("PRIVATE_KEY");

        String jwt = jwtService.createJwt(
                config.getClientId(),
                config.getCertificateId(),
                config.getTokenUrl(),
                config.getScope(),
                privateKey);

        String body = "grant_type=" + URLEncoder.encode("client_credentials", StandardCharsets.UTF_8)
                + "&client_assertion_type="
                + URLEncoder.encode("urn:ietf:params:oauth:client-assertion-type:jwt-bearer", StandardCharsets.UTF_8)
                + "&client_assertion=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getTokenUrl()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch access token: " + response.body());
        }

        return response.body();
    }
}
