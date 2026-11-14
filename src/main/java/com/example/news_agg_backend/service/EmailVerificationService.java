package com.example.news_agg_backend.service;

import com.example.news_agg_backend.model.AccountStatus;
import com.example.news_agg_backend.model.EmailVerificationCode;
import com.example.news_agg_backend.model.Role;
import com.example.news_agg_backend.model.User;
import com.example.news_agg_backend.repository.EmailVerificationCodeRepository;
import com.example.news_agg_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationCodeRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final int MAX_ATTEMPTS = 3;
    private static final int CODE_VALIDITY_MINUTES = 10;

    public void sendVerificationCode(String name, String email, String password) {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail).orElse(null);

        if (user != null) {

            if (user.isEmailVerified()
                    || user.getStatus() != AccountStatus.PENDING) {
                throw new RuntimeException(
                        "Cette adresse e-mail est déjà utilisée."
                );
            }

            // Le compte existe mais n'est pas encore vérifié.
            // On autorise l'envoi d'un nouveau code.

        } else {

            user = User.builder()
                    .name(name.trim())
                    .email(normalizedEmail)
                    .password(passwordEncoder.encode(password))
                    .role(Role.READER)
                    .status(AccountStatus.PENDING)
                    .emailVerified(false)
                    .build();

            userRepository.save(user);
        }

        String code = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        EmailVerificationCode verification = new EmailVerificationCode();

        verification.setEmail(normalizedEmail);
        verification.setCodeHash(passwordEncoder.encode(code));
        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES)
        );
        verification.setAttempts(0);

        verificationRepository.deleteByEmail(normalizedEmail);
        verificationRepository.save(verification);

        emailService.sendRegistrationCode(normalizedEmail, code);
    }

    public void verifyCode(String email, String code) {
        String normalizedEmail = email.trim().toLowerCase();

        EmailVerificationCode verification = verificationRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Code invalide ou expiré.")
                );

        // Vérifier l'expiration
        if (!verification.getExpiresAt().isAfter(LocalDateTime.now())) {
            verificationRepository.deleteByEmail(normalizedEmail);
            throw new RuntimeException("Code expiré. Veuillez demander un nouveau code."
            );
        }

        // Vérifier le nombre de tentatives
        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            verificationRepository.deleteByEmail(normalizedEmail);
            throw new RuntimeException("Nombre maximum de tentatives atteint. Demandez un nouveau code."
            );
        }

        // Vérifier le code
        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            int attempts = verification.getAttempts() + 1;
            verification.setAttempts(attempts);
            if (attempts >= MAX_ATTEMPTS) {
                verificationRepository.deleteByEmail(normalizedEmail);
                throw new RuntimeException("3 tentatives incorrectes. Votre code est invalidé. Demandez un nouveau code."
                );
            }
            verificationRepository.save(verification);
            int remaining = MAX_ATTEMPTS - attempts;
            throw new RuntimeException("Code incorrect. Il vous reste " + remaining + " tentative(s)."
            );
        }

        // Code correct : vérifier le compte
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException("Compte introuvable.")
                );

        user.setEmailVerified(true);
        userRepository.save(user);

        verificationRepository.deleteByEmail(normalizedEmail);
    }
}