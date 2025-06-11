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
    public static ECPrivateKey loadEcPrivateKey(String envVarName) throws Exception {
    String keyPem = System.getenv(envVarName);
    if (keyPem == null) {
        throw new RuntimeException("Environment variable " + envVarName + " not set");
    }
    keyPem = keyPem
            .replaceAll("-----BEGIN (.*)-----", "")
            .replaceAll("-----END (.*)-----", "")
            .replaceAll("\\s", "");
    byte[] keyBytes = Base64.getDecoder().decode(keyPem);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("EC");
    return (ECPrivateKey) kf.generatePrivate(spec);
}
    }
}
