package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface ArticleRepository extends MongoRepository<Article, String> {

    boolean existsByLink(String link);

    Page<Article> findByCategoryIdIn(Collection<String> categoryIds, Pageable pageable);
}