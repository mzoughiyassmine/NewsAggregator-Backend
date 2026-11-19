package com.example.news_agg_backend.dto;

import java.time.LocalDateTime;

// Un article tel qu'il est lu dans un flux RSS, avant d'être enregistré
public record FeedItem(
        String title,
        String link,
        String description,
        LocalDateTime publishedAt,
        String imageUrl
) {}