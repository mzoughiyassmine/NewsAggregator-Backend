package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.SourceRSS;
import com.example.news_agg_backend.model.SourceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SourceRSSRepository extends MongoRepository<SourceRSS, String> {

    boolean existsByUrl(String url);

    boolean existsByUrlAndIdNot(String url, String id);

    List<SourceRSS> findByStatus(SourceStatus status);
}