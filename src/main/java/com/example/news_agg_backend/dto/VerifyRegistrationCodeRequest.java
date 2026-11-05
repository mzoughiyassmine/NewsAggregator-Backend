package com.example.news_agg_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record  VerifyRegistrationCodeRequest (
    @NotBlank(message = "L'adresse e-mail est obligatoire")
    @Email(message = "Veuillez saisir une adresse e-mail valide")
    String email,

    @NotBlank(message = "Le code est obligatoire")
    @Pattern(regexp = "^[0-9]{6}$", message = "Le code doit contenir exactement 6 chiffres")
    String code
) {
}
