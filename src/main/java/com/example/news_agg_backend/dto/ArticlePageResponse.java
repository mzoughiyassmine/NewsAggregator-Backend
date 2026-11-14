package com.example.news_agg_backend.dto;

import java.util.List;

public record ArticlePageResponse(
        List<ArticleResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}