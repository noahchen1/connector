package com.example.connector.aws;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Optional;

@Service
public class LogService {
    private final S3Client s3Client = S3Client
            .builder()
            .region(Region.US_EAST_1)
            .build();

    @PreDestroy
    public void uploadLogFile() {
        String bucketName = "nc-connector-bucket";
        String logsDir = "logs";
        String filePattern = "app-*.log";

        try {
            Optional<Path> latestLog = Files.list(Paths.get(logsDir))
                    .filter(path -> path.getFileName().toString().matches("app-\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.log"))
                    .max(Comparator.comparingLong(path -> path.toFile().lastModified()));

            if (latestLog.isPresent()) {
                Path logFile = latestLog.get();
                System.out.println("Uploading file: " + logFile);

                String key = "logs/" + logFile.getFileName().toString();
                PutObjectResponse response = s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build(),
                        logFile
                );
                System.out.println("S3 upload response: " + response);
            } else {
                System.out.println("No log file found to upload.");
            }
        } catch (IOException e) {
            // Handle exception (e.g., log or rethrow)
            e.printStackTrace();
        }
    }
}