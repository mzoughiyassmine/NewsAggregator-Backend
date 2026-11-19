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
import com.example.news_agg_backend.dto.ChangePasswordRequest;

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
    public LoginResponse readerLogin(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }

        if (user.getRole() != Role.READER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Utilisez l'espace administrateur");
        }

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte bloqué");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
    }
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Utilisateur introuvable"));

        // 400 et non 401 : un 401 serait interprété comme « session expirée »
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ancien mot de passe incorrect");
        }

        validateNewPassword(request.newPassword());

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nouveau mot de passe doit être différent de l'ancien");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private void validateNewPassword(String password) {
        boolean tooWeak = password.length() < 8
                || !password.matches(".*\\p{L}.*")   // au moins une lettre
                || !password.matches(".*\\d.*");     // au moins un chiffre
        if (tooWeak) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mot de passe trop faible : 8 caractères minimum, avec au moins une lettre et un chiffre");
        }
    }
    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Email ou mot de passe incorrect");
    }
}