package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "registration_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationCode {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    // Code haché (BCrypt), jamais en clair
    private String codeHash;

    // MongoDB supprime automatiquement le document après cette date
    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private int attempts;

    // true quand le bon code a été saisi
    private boolean verified;
}