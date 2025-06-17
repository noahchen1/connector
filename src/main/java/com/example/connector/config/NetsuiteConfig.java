package com.example.connector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class NetsuiteConfig {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String clientId = dotenv.get("NS_CLIENT_ID");
    private static final String certificateId = dotenv.get("NS_CERT_ID");

    @Value("${netsuite.token-url}")
    private String tokenUrl;

    @Value("${netsuite.scope}")
    private String scope;

    public String getClientId() {
        return clientId;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getScope() {
        return scope;
    }
}
