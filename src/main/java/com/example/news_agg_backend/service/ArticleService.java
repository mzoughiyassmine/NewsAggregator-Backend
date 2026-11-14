package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.ArticlePageResponse;
import com.example.news_agg_backend.dto.ArticleResponse;
import com.example.news_agg_backend.model.Article;
import com.example.news_agg_backend.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ArticleRepository articleRepository;

    public ArticlePageResponse findPage(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        // Du plus récemment collecté au plus ancien ; l'id départage les articles collectés au même instant
        Sort sort = Sort.by(Sort.Direction.DESC, "collectedAt")
                .and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(safePage, safeSize, sort);

        Page<Article> result = articleRepository.findAll(pageable);

        return new ArticlePageResponse(
                result.getContent().stream().map(ArticleResponse::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}