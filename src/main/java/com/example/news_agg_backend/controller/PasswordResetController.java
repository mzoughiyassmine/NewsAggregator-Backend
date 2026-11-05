package com.example.news_agg_backend.controller;

import com.example.news_agg_backend.dto.ForgotPasswordRequest;
import com.example.news_agg_backend.dto.ResetPasswordRequest;
import com.example.news_agg_backend.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendResetCode(request.email());
        return Map.of("message", "Si le compte existe, un code sera envoyé par e-mail."
        );
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(
                request.email(),
                request.code(),
                request.newPassword()
        );
        return Map.of("message", "Votre mot de passe a été réinitialisé avec succès."
        );
    }
}