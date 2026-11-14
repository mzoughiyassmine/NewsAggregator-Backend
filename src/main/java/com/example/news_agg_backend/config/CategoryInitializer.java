package com.example.news_agg_backend.config;

import com.example.news_agg_backend.model.Category;
import com.example.news_agg_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// S'exécute une fois à chaque démarrage et crée la catégorie par défaut si elle n'existe pas
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryInitializer implements ApplicationRunner {

    // Utilisées aussi par le classifieur (étape B4) pour retrouver la catégorie par défaut
    public static final String DEFAULT_NAME = "Autres";
    public static final String DEFAULT_KEY = "autres";

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.existsByNameKey(DEFAULT_KEY)) {
            return;
        }

        Category other = Category.builder()
                .name(DEFAULT_NAME)
                .nameKey(DEFAULT_KEY)
                .keywords(List.of())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            categoryRepository.save(other);
            log.info("Catégorie par défaut « {} » créée", DEFAULT_NAME);
        } catch (DuplicateKeyException e) {
            // Garde-fou : créée entre le contrôle et la sauvegarde (deux instances qui démarrent)
        }
    }
}