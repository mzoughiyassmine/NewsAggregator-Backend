package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    private String id;

    private String title;

    // Adresse de l'article sur le site d'origine. Unique : sert à détecter les doublons.
    @Indexed(unique = true)
    private String link;

    // Extrait fourni par le flux RSS (peut être vide)
    private String description;

    // Date de publication indiquée par le flux (peut être nulle si le flux ne la fournit pas)
    private LocalDateTime publishedAt;

    // Source RSS d'où vient l'article
    private String sourceId;
    private String sourceName;

    // Date à laquelle notre application a collecté l'article
    private LocalDateTime collectedAt;

    // Catégorie attribuée automatiquement (US14)
    private String categoryId;
    private String categoryName;

    // URL de l'image de l'article (peut être nulle si le flux n'en fournit pas)
    private String imageUrl;
}