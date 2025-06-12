package com.example.connector;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.connector.dto.TokenResponse;
import com.example.connector.netsuite.NetsuiteAuthClient;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ConnectorApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConnectorApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(NetsuiteAuthClient netsuiteAuthClient, NetsuiteCustomerClient netsuiteCustomerClient) {
		return args -> {
			try {
				String tokenResponse = netsuiteAuthClient.fetchAccessToken();
				ObjectMapper mapper = new ObjectMapper();
				TokenResponse response = mapper.readValue(tokenResponse, TokenResponse.class);
				String token = response.getAccess_token();
				String custResponse = netsuiteCustomerClient.getCustomerEmail(token, "149777");
				
				System.out.println("res " + custResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}