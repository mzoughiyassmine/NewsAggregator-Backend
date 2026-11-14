package com.example.news_agg_backend.service;

import com.example.news_agg_backend.config.CategoryInitializer;
import com.example.news_agg_backend.dto.FeedItem;
import com.example.news_agg_backend.model.Article;
import com.example.news_agg_backend.model.Category;
import com.example.news_agg_backend.model.SourceRSS;
import com.example.news_agg_backend.model.SourceStatus;
import com.example.news_agg_backend.repository.ArticleRepository;
import com.example.news_agg_backend.repository.CategoryRepository;
import com.example.news_agg_backend.repository.SourceRSSRepository;
import com.rometools.rome.io.FeedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCollectorService {

    private final SourceRSSRepository sourceRepository;
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final RssFeedReader feedReader;
    private final ArticleClassifier classifier;

    public void collectAll() {
        List<SourceRSS> sources = sourceRepository.findByStatus(SourceStatus.ACTIVE);
        log.info("Collecte démarrée : {} source(s) active(s)", sources.size());

        // Chargées une seule fois par collecte, pas une requête par article
        List<Category> categories = categoryRepository.findAll();
        Category fallback = categories.stream()
                .filter(c -> CategoryInitializer.DEFAULT_KEY.equals(c.getNameKey()))
                .findFirst()
                .orElse(null);

        int newArticles = 0;
        int failedSources = 0;

        for (SourceRSS source : sources) {
            try {
                newArticles += collectFromSource(source, categories, fallback);
            } catch (Exception e) {
                // Une source en échec ne doit pas empêcher le traitement des suivantes
                failedSources++;
                log.error("Échec de la collecte pour la source « {} » ({}) : {}",
                        source.getName(), source.getUrl(), e.toString());
            }
        }

        log.info("Collecte terminée : {} nouvel(s) article(s), {} source(s) en échec",
                newArticles, failedSources);
    }

    private int collectFromSource(SourceRSS source, List<Category> categories, Category fallback)
            throws IOException, FeedException {
        List<FeedItem> items = feedReader.read(source.getUrl());
        int saved = 0;

        for (FeedItem item : items) {
            if (articleRepository.existsByLink(item.link())) {
                continue;
            }

            Category category = classify(item, categories, fallback);

            Article article = Article.builder()
                    .title(item.title())
                    .link(item.link())
                    .description(item.description())
                    .publishedAt(item.publishedAt())
                    .sourceId(source.getId())
                    .sourceName(source.getName())
                    .categoryId(category != null ? category.getId() : null)
                    .categoryName(category != null ? category.getName() : null)
                    .collectedAt(LocalDateTime.now())
                    .build();

            try {
                articleRepository.save(article);
                saved++;
            } catch (DuplicateKeyException e) {
                // Garde-fou : l'article a été enregistré entre le contrôle et la sauvegarde
            }
        }

        log.info("Source « {} » : {} article(s) lu(s), {} nouveau(x)",
                source.getName(), items.size(), saved);
        return saved;
    }

    // Une erreur de classement ne doit jamais empêcher l'enregistrement : on range dans « Autres »
    private Category classify(FeedItem item, List<Category> categories, Category fallback) {
        try {
            return classifier.classify(item.title(), item.description(), categories)
                    .orElse(fallback);
        } catch (Exception e) {
            log.warn("Classification impossible pour « {} » : {}", item.title(), e.toString());
            return fallback;
        }
    }
}