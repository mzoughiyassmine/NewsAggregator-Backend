package com.example.news_agg_backend.service;

import com.example.news_agg_backend.model.Category;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArticleClassifierTest {

    private final ArticleClassifier classifier = new ArticleClassifier();

    private static Category cat(String name, String... keywords) {
        return Category.builder()
                .id(name)
                .name(name)
                .nameKey(name.toLowerCase())
                .keywords(List.of(keywords))
                .build();
    }

    private final List<Category> categories = List.of(
            cat("Autres"),
            cat("Sport", "football", "match", "tennis"),
            cat("Économie", "économie", "bourse", "inflation"),
            cat("Technologie", "ia", "smartphone", "intelligence artificielle"));

    private String nameOf(Optional<Category> result) {
        return result.map(Category::getName).orElse(null);
    }

    @Test
    void classeUnArticleDeSport() {
        var result = classifier.classify("Le match de football de ce soir", null, categories);
        assertEquals("Sport", nameOf(result));
    }

    @Test
    void renvoieVideQuandAucunMotCleNeCorrespond() {
        var result = classifier.classify("La météo du week-end", "Soleil attendu", categories);
        assertTrue(result.isEmpty());
    }

    @Test
    void ignoreLesAccentsEtLaCasse() {
        var result = classifier.classify("ECONOMIE : la croissance ralentit", null, categories);
        assertEquals("Économie", nameOf(result));
    }

    @Test
    void nettoieLeHtmlDeLaDescription() {
        var result = classifier.classify("Résumé du jour",
                "<p>Le <b>tennis</b> &eacute;tait au programme</p>", categories);
        assertEquals("Sport", nameOf(result));
    }

    @Test
    void necherchePasUnMotCleAuMilieuDUnAutreMot() {
        var arts = List.of(cat("Arts", "art"));
        var result = classifier.classify("Le départ du partenaire", null, arts);
        assertTrue(result.isEmpty());
    }

    @Test
    void accepteLePluriel() {
        var result = classifier.classify("Les smartphones arrivent en magasin", null, categories);
        assertEquals("Technologie", nameOf(result));
    }

    @Test
    void reconnaitUnMotCleComposeDeplusieursMots() {
        var result = classifier.classify("L'intelligence artificielle progresse", null, categories);
        assertEquals("Technologie", nameOf(result));
    }

    @Test
    void leTitrePèseplusQueLaDescription() {
        // Titre : 1 mot-clé de sport (3 points). Description : 2 mots-clés d'économie (2 points).
        var result = classifier.classify("Football", "bourse inflation", categories);
        assertEquals("Sport", nameOf(result));
    }

    @Test
    void departageUneEgaliteParOrdreAlphabetique() {
        var tie = List.of(cat("Beta", "x"), cat("Alpha", "x"));
        var result = classifier.classify("x", null, tie);
        assertEquals("Alpha", nameOf(result));
    }

    @Test
    void gereTitreEtDescriptionNuls() {
        assertTrue(classifier.classify(null, null, categories).isEmpty());
    }
}