package com.example.news_agg_backend.config;

import com.example.news_agg_backend.model.AccountStatus;
import com.example.news_agg_backend.model.Role;
import com.example.news_agg_backend.model.User;
import com.example.news_agg_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        String email = adminEmail.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            log.info("Compte administrateur déjà présent : {}", email);
            return;
        }

        User admin = User.builder()
                .name(adminName)
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .status(AccountStatus.ACTIVE)
                .build();

        userRepository.save(admin);
        log.info("Compte administrateur créé : {}", email);
    }
}