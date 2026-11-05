package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.LoginRequest;
import com.example.news_agg_backend.dto.LoginResponse;
import com.example.news_agg_backend.dto.RegisterRequest;
import com.example.news_agg_backend.model.AccountStatus;
import com.example.news_agg_backend.model.Role;
import com.example.news_agg_backend.model.User;
import com.example.news_agg_backend.repository.UserRepository;
import com.example.news_agg_backend.model.Topic;
import com.example.news_agg_backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElseThrow(this::invalidCredentials);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }
        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte bloqué");
        }
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    public LoginResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune inscription en attente pour cette adresse e-mail."));
        if (user.getRole() != Role.READER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette adresse e-mail ne peut pas être utilisée pour une inscription Reader.");
        }
        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Veuillez d'abord vérifier votre adresse e-mail.");
        }
        if (user.getStatus() != AccountStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette inscription a déjà été finalisée.");
        }
        if (request.topicCodes() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez sélectionner au moins 3 centres d'intérêt.");
        }
        List<String> topics = request.topicCodes().stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (topics.size() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sélectionnez au moins 3 centres d'intérêt.");
        }
        List<Topic> existingTopics = topicRepository.findByCodeIn(topics);
        Set<String> existingCodes = existingTopics.stream()
                .map(Topic::getCode)
                .collect(Collectors.toSet());
        if (!existingCodes.containsAll(topics)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un ou plusieurs centres d'intérêt sélectionnés sont invalides.");
        }
        if (request.language() == null || request.language().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez sélectionner une langue.");
        }
        String language = request.language().trim().toLowerCase();
        if (!Set.of("fr", "ar", "en").contains(language)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Langue non prise en charge.");
        }
        user.setTopicCodes(topics);
        user.setLanguage(language);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
    }
    // Connexion Reader
    public LoginResponse readerLogin(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElseThrow(this::invalidCredentials);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }
        if (user.getRole() != Role.READER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux lecteurs");
        }
        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Veuillez vérifier votre adresse e-mail avant de vous connecter.");
        }
        if (user.getStatus() == AccountStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Veuillez terminer votre inscription.");
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte est bloqué");
        }
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
    }
}
