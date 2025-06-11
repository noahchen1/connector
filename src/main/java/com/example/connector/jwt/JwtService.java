package com.example.connector.jwt;

import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.UUID;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {
    public String createJwt(String clientId, String certificateId, String audience, String scope,
            ECPrivateKey privateKey) throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(clientId)
                .subject(clientId)
                .audience(audience)
                .claim("scope", scope)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)
                .keyID(certificateId)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);
        ECDSASigner signer = new ECDSASigner(privateKey);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}
