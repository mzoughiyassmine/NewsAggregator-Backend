package com.example.news_agg_backend.controller;

import com.example.news_agg_backend.dto.LoginRequest;
import com.example.news_agg_backend.dto.LoginResponse;
import com.example.news_agg_backend.dto.RegisterRequest;
import com.example.news_agg_backend.dto.SendRegistrationCodeRequest;
import com.example.news_agg_backend.dto.VerifyRegistrationCodeRequest;
import com.example.news_agg_backend.service.AuthService;
import com.example.news_agg_backend.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/reader/login")
    public LoginResponse readerLogin(@Valid @RequestBody LoginRequest request) {
        return authService.readerLogin(request);
    }

    @PostMapping("/send-registration-code")
    public void sendRegistrationCode(@Valid @RequestBody SendRegistrationCodeRequest request) {
        registrationService.sendCode(request.email());
    }

    @PostMapping("/verify-registration-code")
    public void verifyRegistrationCode(@Valid @RequestBody VerifyRegistrationCodeRequest request) {
        registrationService.verifyCode(request.email(), request.code());
    }

    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        return registrationService.register(request);
    }
}