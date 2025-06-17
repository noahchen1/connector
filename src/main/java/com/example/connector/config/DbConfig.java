package com.example.connector.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DbConfig {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String DB_HOST = dotenv.get("DB_HOST");
    private static final String DB_USER = dotenv.get("DB_USERNAME");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    public static String getDbHost() {
        return DB_HOST;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }
}
