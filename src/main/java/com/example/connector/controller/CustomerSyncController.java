package com.example.connector.controller;

import com.example.connector.aws.LogService;
import com.example.connector.dto.TokenResponseDto;
import com.example.connector.netsuite.NetsuiteAuthClient;
import com.example.connector.netsuite.NetsuiteCustomerClient;
import com.example.connector.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomerSyncController {
    @Autowired
    private NetsuiteAuthClient netsuiteAuthClient;

    @Autowired
    private NetsuiteCustomerClient netsuiteCustomerClient;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LogService logService;

    @PostMapping("/sync-customers")
    public String SyncCustomers() throws Exception {
        try {
            String tokenRes = netsuiteAuthClient.fetchAccessToken();
            ObjectMapper mapper = new ObjectMapper();
            TokenResponseDto parsedRes = mapper.readValue(tokenRes, TokenResponseDto.class);
            String accessToken = parsedRes.getAccess_token();
            customerService.syncCustomers(accessToken, netsuiteCustomerClient);
            logService.uploadLogFile();

            return "Process finished!";
        } catch (Exception e) {
            return "Sync failed: " + e.getMessage();
        }
    }
}
