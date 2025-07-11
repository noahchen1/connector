package com.example.connector;

import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.connector.dto.CustomerDto;
import com.example.connector.dto.CustomerItemDto;
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

    @Autowired
    private ApplicationContext context;

    private long expiresIn;

    @Override
    public void run(String... args) {
        try {
            String tokenRes = netsuiteAuthClient.fetchAccessToken();
            ObjectMapper mapper = new ObjectMapper();
            TokenResponseDto parsedRes = mapper.readValue(tokenRes, TokenResponseDto.class);
            String accessToken = parsedRes.getAccess_token();
            customerService.syncCustomers(accessToken, netsuiteCustomerClient);
            System.out.println("process finished!");
        } catch (Exception e) {
            System.err.println("Sync failed: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
    }

    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        System.setProperty("logging.file.name", "logs/app-" + timestamp + ".log");
        System.setProperty("aws.accessKeyId", dotenv.get("AWS_ACCESS_KEY_ID"));
        System.setProperty("aws.secretAccessKey", dotenv.get("AWS_SECRET_ACCESS_KEY"));
        SpringApplication.run(ConnectorApplication.class, args);
    }
}

// ssh -i /d/repo/default.pem -L
// 15432:database-1.c8522k8ughqc.us-east-1.rds.amazonaws.com:5432
// ec2-user@54.197.108.8