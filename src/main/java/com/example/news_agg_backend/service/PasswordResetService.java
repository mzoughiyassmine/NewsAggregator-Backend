package com.example.news_agg_backend.service;

import com.example.news_agg_backend.model.PasswordResetCode;
import com.example.news_agg_backend.repository.PasswordResetCodeRepository;
import com.example.news_agg_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public void sendResetCode(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        var user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException("Compte introuvable"));

        if (user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Opération non autorisée");
        }

        String code = String.format("%06d",secureRandom.nextInt(1_000_000)
        );

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setEmail(normalizedEmail);
        resetCode.setCodeHash(passwordEncoder.encode(code));
        resetCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        resetCodeRepository.deleteByEmail(normalizedEmail);
        resetCodeRepository.save(resetCode);
        emailService.sendResetCode(normalizedEmail, code);
    }

    public void resetPassword(String email, String code, String newPassword) {
        String normalizedEmail = email.trim().toLowerCase();
        PasswordResetCode resetCode = resetCodeRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Code invalide ou expiré"));
        if (resetCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            resetCodeRepository.deleteByEmail(normalizedEmail);
            throw new RuntimeException("Code expiré");
        }
        if (!passwordEncoder.matches(code, resetCode.getCodeHash())) {
            throw new RuntimeException("Code incorrect");
        }
        var user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetCodeRepository.deleteByEmail(normalizedEmail);
    }



}
