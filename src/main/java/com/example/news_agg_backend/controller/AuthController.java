
package com.example.news_agg_backend.controller;
import com.example.news_agg_backend.dto.LoginRequest;
import com.example.news_agg_backend.dto.LoginResponse;
import com.example.news_agg_backend.dto.RegisterRequest;
import com.example.news_agg_backend.dto.SendRegistrationCodeRequest;
import com.example.news_agg_backend.dto.VerifyRegistrationCodeRequest;
import com.example.news_agg_backend.service.AuthService;
import com.example.news_agg_backend.service.EmailVerificationService;
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
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @PostMapping("/send-registration-code")
    public Map<String, String> sendRegistrationCode(@Valid @RequestBody SendRegistrationCodeRequest request) {
        emailVerificationService.sendVerificationCode(
                request.name(),
                request.email(),
                request.password()
        );
        return Map.of("message", "Code de vérification envoyé avec succès.");
    }
    @PostMapping("/verify-registration-code")
    public Map<String, String> verifyRegistrationCode(@Valid @RequestBody VerifyRegistrationCodeRequest request) {
        emailVerificationService.verifyCode(
                request.email(),
                request.code()
        );
        return Map.of("message", "Adresse e-mail vérifiée avec succès.");
    }
    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    @PostMapping("/reader/login")
    public LoginResponse readerLogin(@Valid @RequestBody LoginRequest request) {
        return authService.readerLogin(request);
    }
}
