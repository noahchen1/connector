package com.example.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.connector.dto.TokenResponseDto;
import com.example.connector.netsuite.NetsuiteAuthClient;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ConnectorApplication implements CommandLineRunner {
	@Autowired
	private NetsuiteAuthClient netsuiteAuthClient;

	@Autowired
	private NetsuiteCustomerClient netsuiteCustomerClient;

	private String accessToken;
	private long expiresIn;

	@Override
	public void run(String... args) throws Exception {
		try {
			String tokenRes = netsuiteAuthClient.fetchAccessToken();
			ObjectMapper mapper = new ObjectMapper();
			TokenResponseDto parsedRes = mapper.readValue(tokenRes, TokenResponseDto.class);
			accessToken = parsedRes.getAccess_token();
			expiresIn = System.currentTimeMillis() + parsedRes.getExpires_in();
			// String custResponse = netsuiteCustomerClient.getCustomerEmail(token, "149777");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConnectorApplication.class, args);
	}
}



