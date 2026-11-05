
package com.example.news_agg_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterRequest(

        @NotBlank(message = "Le nom est obligatoire")
        String name,

        @NotBlank(message = "L'adresse e-mail est obligatoire")
        @Email(message = "Veuillez saisir une adresse e-mail valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$",
                message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial"
        )
        String password,

        @Size(min = 3, message = "Veuillez sélectionner au moins 3 centres d'intérêt")
        List<String> topicCodes,

        @NotBlank(message = "Veuillez sélectionner une langue")
        String language

) {
}
