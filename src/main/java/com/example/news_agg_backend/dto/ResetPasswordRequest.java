package com.example.news_agg_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "L'adresse e-mail est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,

        @NotBlank(message = "Le code est obligatoire")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "Le code doit contenir 6 chiffres"
        )
        String code,

        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$",
                message = "Le mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractère spécial"
        )
        String newPassword

) {}