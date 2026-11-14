package com.example.news_agg_backend.scheduler;

import com.example.news_agg_backend.service.ArticleCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCollectionScheduler {

    private final ArticleCollectorService collector;

    @Scheduled(
            fixedDelayString = "${app.collector.interval-ms}",
            initialDelayString = "${app.collector.initial-delay-ms}")
    public void collect() {
        collector.collectAll();
    }
}