package com.example.news_agg_backend.dto;

import com.example.news_agg_backend.model.Category;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryResponse(
        String id,
        String name,
        List<String> keywords,
        LocalDateTime createdAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                // Les anciennes catégories n'ont pas de mots-clés : on renvoie une liste vide, jamais null
                category.getKeywords() != null ? category.getKeywords() : List.of(),
                category.getCreatedAt()
        );
    }
}