package com.example.news_agg_backend.model;

public enum AccountStatus {
    ACTIVE,
    BLOCKED,
    // Compte créé, e-mail pas encore vérifié : connexion impossible
    PENDING
}