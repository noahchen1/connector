package com.example.connector;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

// ...existing code...

import java.security.KeyFactory;
import java.security.PrivateKey;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class ConnectorApplication {

	public static void main(String[] args) throws Exception {
		// 1. Load private key from resources
		InputStream is = ConnectorApplication.class.getClassLoader().getResourceAsStream("private.pem");
		if (is == null)
			throw new RuntimeException("private.pem not found in resources");
		String keyPem = new String(is.readAllBytes(), StandardCharsets.UTF_8)
				.replaceAll("-----BEGIN (.*)-----", "")
				.replaceAll("-----END (.*)-----", "")
				.replaceAll("\\s", "");
		byte[] keyBytes = Base64.getDecoder().decode(keyPem);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA"); // Use "RSA" if your key is RSA
		PrivateKey privateKey = kf.generatePrivate(spec);

		// 2. Build JWT
		String clientId = "8e9ea88e9307d5b0208e688500b93928ebe980c726b64b28b85138961a8b16ff";
		String tokenUrl = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/auth/oauth2/v1/token";
		String audience = tokenUrl;
		Instant now = Instant.now();

		JWTClaimsSet claims = new JWTClaimsSet.Builder()
				.issuer(clientId)
				.subject(clientId)
				.audience(audience)
				.jwtID(UUID.randomUUID().toString())
				.issueTime(java.util.Date.from(now))
				.expirationTime(java.util.Date.from(now.plusSeconds(300)))
				.build();

		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.PS256).type(JOSEObjectType.JWT).build();
		SignedJWT signedJWT = new SignedJWT(header, claims);
		JWSSigner signer = new com.nimbusds.jose.crypto.RSASSASigner(privateKey); // Use RSASSASigner for RSA
		signedJWT.sign(signer);

		String jwt = signedJWT.serialize();

		// 3. Prepare HTTP request

		String body = "grant_type=" + URLEncoder.encode("client_credentials", StandardCharsets.UTF_8)
				+ "&client_assertion_type="
				+ URLEncoder.encode("urn:ietf:params:oauth:client-assertion-type:jwt-bearer", StandardCharsets.UTF_8)
				+ "&client_assertion=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

		System.out.println(body);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(tokenUrl))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());

		SpringApplication.run(ConnectorApplication.class, args);
	}
}

// POST /services/rest/auth/oauth2/v1/token HTTP/1.1
// Host: <accountID>.suitetalk.api.netsuite.com
// Content-Type: application/x-www-form-urlencoded
//
// grant_type=client_credentials
// &client_assertion_type=urn%3Aietf%3Aparams%3Aoauth%3Aclient-assertion-type%3Ajwt-bearer
// &client_assertion=eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiIsImtpZCI6IkhScmlsN1Z3a2tXMnkwd0F6RlI5R0trMUxWVEhTWlBlcTVFZVdUMkZna3MifQ.eyJpc3MiOiIxYzM0M2E3MTZjMWRjZWI2MGU3ZmMxNDlmYTY3MzU5MjllZjc3ZDI4ZmUxNjI5M2Y4OTI5NzZkZGU3ZDhlM2UyIiwic2NvcGUiOlsicmVzdGxldHMiLCAicmVzdF93ZWJzZXJ2aWNlcyJdLCJhdWQiOiJodHRwczovLzM4Mjk4NTUucmVzdGxldHMuYXBpLm5ldHN1aXRlLmNvbS9zZXJ2aWNlcy9yZXN0L2F1dGgvb2F1dGgyL3YxL3Rva2VuIiwgImp0aSI6ImJaQnFoQThNQzZVMHVrZHNtUGNwMUtIRyIsImV4cCI6MTc0MjU3ODAzMCwiaWF0IjoxNzQyNTc0NDMwfQ.AdTFyKGKeNzVYM5ITiRU_-a4Umlw77y3Td1n8FM6usLPWE6Dt2b2JN1GyCXYCHHKD-FR13-xQLJlMA30nNIKneJIAX57xLpHsFfho-5LdAL6nEm4vdBcOJs3X5sUeEF6r_5Bo53_ghBwlWTfVTsXr_OvY55YqpVDKV-OZjS8LaAAxYF7