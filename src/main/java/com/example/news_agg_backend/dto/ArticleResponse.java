package com.example.news_agg_backend.dto;

import com.example.news_agg_backend.model.Article;

import java.time.LocalDateTime;

public record ArticleResponse(
        String id,
        String title,
        String link,
        String description,
        LocalDateTime publishedAt,
        String sourceId,
        String sourceName,
        String categoryId,
        String categoryName,
        LocalDateTime collectedAt,
        String imageUrl   // à la fin des composants du record

) {

    public static ArticleResponse from(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getLink(),
                article.getDescription(),
                article.getPublishedAt(),
                article.getSourceId(),
                article.getSourceName(),
                article.getCategoryId(),
                article.getCategoryName(),
                article.getCollectedAt(),
                article.getImageUrl()   // au même endroit dans l'appel du constructeur de from()
        );
    }
}