package com.example.connector;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.connector.dto.CustomerDto;
import com.example.connector.dto.TokenResponseDto;
import com.example.connector.netsuite.NetsuiteAuthClient;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.example.connector.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ConnectorApplication implements CommandLineRunner {
	@Autowired
	private NetsuiteAuthClient netsuiteAuthClient;

	@Autowired
	private NetsuiteCustomerClient netsuiteCustomerClient;

	@Autowired
	private CustomerService customerService;

	private String accessToken;
	private long expiresIn;

	@Override
	public void run(String... args) throws Exception {
		try {
			String tokenRes = netsuiteAuthClient.fetchAccessToken();
			ObjectMapper mapper = new ObjectMapper();
			TokenResponseDto parsedRes = mapper.readValue(tokenRes, TokenResponseDto.class);
			accessToken = parsedRes.getAccess_token();

			// expiresIn = System.currentTimeMillis() + parsedRes.getExpires_in();
			// String custResponse = netsuiteCustomerClient.getCustomerEmail(accessToken,
			// "149777");

			// customerService.syncCustomers(accessToken, netsuiteCustomerClient);

			System.out.println("Access Token: " + accessToken);
			CustomerDto customerDto = new CustomerDto();

			customerDto.setFirstname("John");
			customerDto.setLastname("Doe");
			customerDto.setEmail("nuoya19960309@gmail.com");

			List<CustomerDto> customerList = new ArrayList<>();
			customerList.add(customerDto);

			netsuiteCustomerClient.createCustomers(accessToken, customerList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConnectorApplication.class, args);
	}
}

// ssh -i /d/repo/default.pem -L 15432:<rds-endpoint>:5432 ec2-user@54.197.108.8