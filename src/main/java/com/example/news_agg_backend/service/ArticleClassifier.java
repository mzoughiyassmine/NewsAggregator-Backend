package com.example.news_agg_backend.service;

import com.example.news_agg_backend.model.Category;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class ArticleClassifier {

    // Un mot-clé trouvé dans le titre pèse plus que dans la description
    private static final int TITLE_WEIGHT = 3;
    private static final int DESCRIPTION_WEIGHT = 1;

    private static final Pattern HTML_TAG = Pattern.compile("<[^>]*>");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NOT_LETTER_OR_DIGIT = Pattern.compile("[^\\p{L}\\p{N}]+");

    /**
     * Renvoie la catégorie dont les mots-clés correspondent le mieux à l'article,
     * ou un Optional vide si aucun mot-clé n'apparaît (l'appelant choisit alors « Autres »).
     */
    public Optional<Category> classify(String title, String description, List<Category> categories) {
        String titleText = " " + normalize(title) + " ";
        String descriptionText = " " + normalize(description) + " ";

        Category best = null;
        int bestScore = 0;

        // Ordre fixe : en cas d'égalité, la première catégorie de la liste l'emporte (résultat prévisible)
        List<Category> ordered = categories.stream()
                .sorted(Comparator.comparing(Category::getNameKey))
                .toList();

        for (Category category : ordered) {
            int score = score(category, titleText, descriptionText);
            if (score > bestScore) {
                best = category;
                bestScore = score;
            }
        }
        return Optional.ofNullable(best);
    }

    private int score(Category category, String titleText, String descriptionText) {
        if (category.getKeywords() == null) {
            return 0;
        }
        int score = 0;
        for (String raw : category.getKeywords().stream().distinct().toList()) {
            String keyword = normalize(raw);
            if (keyword.isEmpty()) {
                continue;
            }
            if (containsWord(titleText, keyword)) {
                score += TITLE_WEIGHT;
            }
            if (containsWord(descriptionText, keyword)) {
                score += DESCRIPTION_WEIGHT;
            }
        }
        return score;
    }

    // Mot entier uniquement (« art » ne correspond pas à « départ »), avec un « s » final toléré
    private boolean containsWord(String paddedText, String keyword) {
        return paddedText.contains(" " + keyword + " ")
                || paddedText.contains(" " + keyword + "s ");
    }

    // HTML retiré, accents retirés, minuscules, ponctuation remplacée par des espaces
    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String text = HTML_TAG.matcher(value).replaceAll(" ");
        text = HtmlUtils.htmlUnescape(text);                 // &eacute; -> é, &amp; -> &
        text = HTML_TAG.matcher(text).replaceAll(" ");       // cas du HTML encodé deux fois
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = DIACRITICS.matcher(text).replaceAll("");
        text = text.toLowerCase(Locale.ROOT);
        text = NOT_LETTER_OR_DIGIT.matcher(text).replaceAll(" ");
        return text.trim();
    }
}