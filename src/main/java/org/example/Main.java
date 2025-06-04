package org.example;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws Exception {
        String tokenUrl = "https://5405357-sb1.suitetalk.api.netsuite.com/services/rest/auth/oauth2/v1/token";
        String clientId = "8e9ea88e9307d5b0208e688500b93928ebe980c726b64b28b85138961a8b16ff";
        String clientSecret = "4d297f9ca85d206da31ab686ec224f750762208ee44a1dc033a9769cd1668999";
        String jwtToken = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgZT139tru6LD0AppGsRtvzMf5IfkR8E0R53TIFjC5p02hRANCAAQkpKpicUikCs8rb+MT3pfBERRDWM38TgyYMCnpP2eFNZJhf02uSod6/exCCjVEkBBtUCWa+DwgGVhzC4mbcJng";
        String body = "grant_type=client_credentials"
                + "&client_assertion_type=urn%3Aietf%3Aparams%3Aoauth%3Aclient-assertion-type%3Ajwt-bearer"
                + "&client_assertion=" + jwtToken;

        String pemPath = "private.pem"; // Path to your PEM file

        String jwt = JWTGenerator.generateJWT(pemPath, clientId, tokenUrl);
        System.out.println("Generated JWT:\n" + jwt);

//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(tokenUrl))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .header("Authorization", "Bearer " + jwtToken)
//                .POST(HttpRequest.BodyPublishers.ofString(body))
//                .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());
    }
}


//POST /services/rest/auth/oauth2/v1/token HTTP/1.1
//Host: <accountID>.suitetalk.api.netsuite.com
//Content-Type: application/x-www-form-urlencoded
//
//        grant_type=client_credentials
//        &client_assertion_type=urn%3Aietf%3Aparams%3Aoauth%3Aclient-assertion-type%3Ajwt-bearer
//&client_assertion=eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiIsImtpZCI6IkhScmlsN1Z3a2tXMnkwd0F6RlI5R0trMUxWVEhTWlBlcTVFZVdUMkZna3MifQ.eyJpc3MiOiIxYzM0M2E3MTZjMWRjZWI2MGU3ZmMxNDlmYTY3MzU5MjllZjc3ZDI4ZmUxNjI5M2Y4OTI5NzZkZGU3ZDhlM2UyIiwic2NvcGUiOlsicmVzdGxldHMiLCAicmVzdF93ZWJzZXJ2aWNlcyJdLCJhdWQiOiJodHRwczovLzM4Mjk4NTUucmVzdGxldHMuYXBpLm5ldHN1aXRlLmNvbS9zZXJ2aWNlcy9yZXN0L2F1dGgvb2F1dGgyL3YxL3Rva2VuIiwgImp0aSI6ImJaQnFoQThNQzZVMHVrZHNtUGNwMUtIRyIsImV4cCI6MTc0MjU3ODAzMCwiaWF0IjoxNzQyNTc0NDMwfQ.AdTFyKGKeNzVYM5ITiRU_-a4Umlw77y3Td1n8FM6usLPWE6Dt2b2JN1GyCXYCHHKD-FR13-xQLJlMA30nNIKneJIAX57xLpHsFfho-5LdAL6nEm4vdBcOJs3X5sUeEF6r_5Bo53_ghBwlWTfVTsXr_OvY55YqpVDKV-OZjS8LaAAxYF7

