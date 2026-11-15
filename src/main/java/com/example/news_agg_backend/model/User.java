package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    // Contient le mot de passe HACHÉ (BCrypt), jamais le mot de passe en clair
    private String password;

    private Role role;

    private AccountStatus status;

    // Passe à true quand l'utilisateur a saisi le bon code reçu par e-mail
    private boolean emailVerified;

    // Centres d'intérêt choisis à l'inscription (codes des topics créés par l'admin)
    @Builder.Default
    private List<String> categoryIds = new ArrayList<>();
}