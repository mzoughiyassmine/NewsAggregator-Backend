package com.example.news_agg_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CategoryRequest(
        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
        String name,

        // Facultatif : une catégorie peut être créée sans mot-clé
        @Size(max = 20, message = "Une catégorie ne peut pas avoir plus de 20 mots-clés")
        List<@Size(max = 30, message = "Un mot-clé ne doit pas dépasser 30 caractères") String> keywords
) {
}