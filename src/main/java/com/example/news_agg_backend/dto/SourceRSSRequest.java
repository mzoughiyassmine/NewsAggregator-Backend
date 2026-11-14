package com.example.news_agg_backend.dto;

import com.example.news_agg_backend.model.SourceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SourceRSSRequest(

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String name,

        @NotBlank(message = "L'URL du flux RSS est obligatoire")
        @Size(max = 500, message = "L'URL ne doit pas dépasser 500 caractères")
        @Pattern(regexp = "^https?://[^\\s/$.?#][^\\s]*$",
                message = "L'URL doit commencer par http:// ou https:// et être valide")
        String url,

        @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
        String description,

        SourceStatus status
) {
}