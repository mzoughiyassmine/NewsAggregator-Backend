package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.LoginRequest;
import com.example.news_agg_backend.dto.LoginResponse;
import com.example.news_agg_backend.model.AccountStatus;
import com.example.news_agg_backend.model.Role;
import com.example.news_agg_backend.model.User;
import com.example.news_agg_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }

        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès réservé aux administrateurs");
        }

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Compte bloqué");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Email ou mot de passe incorrect");
    }
}