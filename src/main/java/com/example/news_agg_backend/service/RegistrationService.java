package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.LoginResponse;
import com.example.news_agg_backend.dto.RegisterRequest;
import com.example.news_agg_backend.model.AccountStatus;
import com.example.news_agg_backend.model.RegistrationCode;
import com.example.news_agg_backend.model.Role;
import com.example.news_agg_backend.model.User;
import com.example.news_agg_backend.repository.CategoryRepository;
import com.example.news_agg_backend.repository.RegistrationCodeRepository;
import com.example.news_agg_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.mail.MailException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private static final int CODE_VALIDITY_MINUTES = 10;
    private static final int VERIFIED_VALIDITY_MINUTES = 30;
    private static final int MAX_ATTEMPTS = 5;
    private static final int MIN_CATEGORIES = 3;

    private final SecureRandom random = new SecureRandom();

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RegistrationCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final EmailService emailService;


    public void sendCode(String rawEmail) {
        String email = normalize(rawEmail);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un compte existe déjà avec cet e-mail");
        }

        String code = String.format("%06d", random.nextInt(1_000_000));

        codeRepository.deleteByEmail(email);
        codeRepository.save(RegistrationCode.builder()
                .email(email)
                .codeHash(passwordEncoder.encode(code))
                .expiresAt(Instant.now().plus(CODE_VALIDITY_MINUTES, ChronoUnit.MINUTES))
                .attempts(0)
                .verified(false)
                .build());

        // À supprimer une fois l'envoi d'e-mail validé : un code en clair ne doit pas rester dans les logs
        log.info("Code de vérification pour {} : {}", email, code);

        try {
            emailService.sendRegistrationCode(email, code);        } catch (MailException e) {
            codeRepository.deleteByEmail(email);
            log.error("Échec d'envoi de l'e-mail à {}", email, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Impossible d'envoyer l'e-mail de vérification. Réessayez plus tard.");
        }
    }

    public void verifyCode(String rawEmail, String code) {
        String email = normalize(rawEmail);

        RegistrationCode rc = codeRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Aucun code demandé pour cet e-mail"));

        if (rc.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expiré");
        }
        if (rc.getAttempts() >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Trop de tentatives. Demandez un nouveau code.");
        }
        if (!passwordEncoder.matches(code.trim(), rc.getCodeHash())) {
            rc.setAttempts(rc.getAttempts() + 1);
            codeRepository.save(rc);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code incorrect");
        }

        rc.setVerified(true);
        rc.setExpiresAt(Instant.now().plus(VERIFIED_VALIDITY_MINUTES, ChronoUnit.MINUTES));
        codeRepository.save(rc);
    }

    public LoginResponse register(RegisterRequest request) {
        String email = normalize(request.email());

        RegistrationCode rc = codeRepository.findByEmail(email)
                .filter(RegistrationCode::isVerified)
                .filter(c -> c.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "E-mail non vérifié ou code expiré"));

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un compte existe déjà avec cet e-mail");
        }

        List<String> ids = request.categoryIds().stream().distinct().toList();
        if (ids.size() < MIN_CATEGORIES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Veuillez choisir au moins " + MIN_CATEGORIES + " centres d'intérêt");
        }
        if (categoryRepository.findAllById(ids).size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Centre d'intérêt inconnu");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.READER)
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .categoryIds(ids)
                .build();
        userRepository.save(user);

        codeRepository.delete(rc);

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}