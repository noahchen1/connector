package com.example.connector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NetsuiteConfig {
    @Value("${netsuite.client-id}")
    private String clientId;

    @Value("${netsuite.certificate-id}")
    private String certificateId;

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
