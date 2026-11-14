package com.example.news_agg_backend.config;

import com.example.news_agg_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TempMailRun implements CommandLineRunner {

    private final EmailService emailService;

    @Override
    public void run(String... args) {
        // Remplace par ta propre adresse
        emailService.sendRegistrationCode("ton.adresse@exemple.com", "123456");
    }
}