package com.example.news_agg_backend.dto;

import com.example.news_agg_backend.model.SourceRSS;
import com.example.news_agg_backend.model.SourceStatus;

import java.time.LocalDateTime;

public record SourceRSSResponse(
        String id,
        String name,
        String url,
        String description,
        SourceStatus status,
        LocalDateTime createdAt
) {

    public static SourceRSSResponse from(SourceRSS source) {
        return new SourceRSSResponse(
                source.getId(),
                source.getName(),
                source.getUrl(),
                source.getDescription(),
                source.getStatus(),
                source.getCreatedAt()
        );
    }
}