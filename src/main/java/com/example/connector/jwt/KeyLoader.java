package com.example.connector.jwt;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import com.example.connector.ConnectorApplication;

public class KeyLoader {
    public static ECPrivateKey loadEcPrivateKey(String resourcePath) throws Exception {
        InputStream is = ConnectorApplication.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null)
            throw new RuntimeException("private.pem not found in resources");
        // String keyPem = System.getenv(envVarName); for deployment
        String keyPem = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(keyPem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PrivateKey privateKey = kf.generatePrivate(spec);

        return (ECPrivateKey) privateKey;
    }
}
