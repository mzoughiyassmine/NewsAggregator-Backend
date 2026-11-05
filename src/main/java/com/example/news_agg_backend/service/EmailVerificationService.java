
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

    public void sendVerificationCode(String name, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Cette adresse e-mail est déjà utilisée.");
        }
        User user = User.builder()
                .name(name.trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(password))
                .role(Role.READER)
                .status(AccountStatus.PENDING)
                .emailVerified(false)
                .build();
        userRepository.save(user);
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        EmailVerificationCode verification = new EmailVerificationCode();
        verification.setEmail(normalizedEmail);
        verification.setCodeHash(passwordEncoder.encode(code));
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        verificationRepository.deleteByEmail(normalizedEmail);
        verificationRepository.save(verification);
        emailService.sendRegistrationCode(normalizedEmail, code);
    }

    public void verifyCode(String email, String code) {
        String normalizedEmail = email.trim().toLowerCase();
        EmailVerificationCode verification = verificationRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Code invalide ou expiré."));
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationRepository.deleteByEmail(normalizedEmail);
            throw new RuntimeException("Code expiré. Veuillez recommencer.");
        }
        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            throw new RuntimeException("Code incorrect.");
        }
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Compte introuvable."));
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationRepository.deleteByEmail(normalizedEmail);
    }
}
