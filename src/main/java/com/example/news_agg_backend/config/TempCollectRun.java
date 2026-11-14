package com.example.news_agg_backend.config;

import com.example.news_agg_backend.service.ArticleCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TempCollectRun implements CommandLineRunner {

    private final ArticleCollectorService collector;

    @Override
    public void run(String... args) {
        collector.collectAll();
    }
}