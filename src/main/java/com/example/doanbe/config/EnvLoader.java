package com.example.doanbe.config;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class EnvLoader {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // trỏ đến nơi chứa .env
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Gán biến vào system properties
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue()));
    }
}
