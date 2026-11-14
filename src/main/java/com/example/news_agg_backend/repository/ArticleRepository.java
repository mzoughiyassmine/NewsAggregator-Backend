package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<Article, String> {

    boolean existsByLink(String link);
}