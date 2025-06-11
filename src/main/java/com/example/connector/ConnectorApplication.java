package com.example.connector;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.connector.netsuite.NetsuiteAuthClient;

@SpringBootApplication
public class ConnectorApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConnectorApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(NetsuiteAuthClient netsuiteAuthClient) {
		return args -> {
			try {
				String tokenResponse = netsuiteAuthClient.fetchAccessToken();
				System.out.println("Access Token Response: " + tokenResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}