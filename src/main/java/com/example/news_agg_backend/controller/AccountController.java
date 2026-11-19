package com.example.news_agg_backend.controller;

import com.example.news_agg_backend.dto.ChangePasswordRequest;
import com.example.news_agg_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;

    @PutMapping("/password")
    public Map<String, String> changePassword(Authentication auth,
                                              @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(auth.getName(), request);
        return Map.of("message", "Mot de passe modifié");
    }
}