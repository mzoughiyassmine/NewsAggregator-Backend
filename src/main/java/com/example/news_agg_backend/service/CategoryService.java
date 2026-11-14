package com.example.news_agg_backend.service;

import com.example.news_agg_backend.config.CategoryInitializer;
import com.example.news_agg_backend.dto.CategoryRequest;
import com.example.news_agg_backend.dto.CategoryResponse;
import com.example.news_agg_backend.model.Article;
import com.example.news_agg_backend.model.Category;
import com.example.news_agg_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MongoTemplate mongoTemplate;

    public CategoryResponse create(CategoryRequest request) {
        String name = cleanName(request.name());
        String nameKey = name.toLowerCase(Locale.ROOT);

        if (categoryRepository.existsByNameKey(nameKey)) {
            throw alreadyExists();
        }

        Category category = Category.builder()
                .name(name)
                .nameKey(nameKey)
                .keywords(cleanKeywords(request.keywords()))
                .createdAt(LocalDateTime.now())
                .build();

        try {
            return CategoryResponse.from(categoryRepository.save(category));
        } catch (DuplicateKeyException e) {
            throw alreadyExists();
        }
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "nameKey"))
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse update(String id, CategoryRequest request) {
        Category category = getOrThrow(id);
        ensureNotDefault(category, "modifiée");

        String name = cleanName(request.name());
        String nameKey = name.toLowerCase(Locale.ROOT);

        // On exclut la catégorie elle-même : « Sport » -> « SPORT » reste autorisé
        if (categoryRepository.existsByNameKeyAndIdNot(nameKey, id)) {
            throw alreadyExists();
        }

        category.setName(name);
        category.setNameKey(nameKey);
        category.setKeywords(cleanKeywords(request.keywords()));

        Category saved;
        try {
            saved = categoryRepository.save(category);
        } catch (DuplicateKeyException e) {
            throw alreadyExists();
        }

        // Les articles gardent une copie du nom : on la met à jour
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("categoryId").is(id)),
                Update.update("categoryName", saved.getName()),
                Article.class);

        return CategoryResponse.from(saved);
    }

    public void delete(String id) {
        Category category = getOrThrow(id);
        ensureNotDefault(category, "supprimée");

        Category fallback = categoryRepository.findByNameKey(CategoryInitializer.DEFAULT_KEY)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Catégorie par défaut introuvable"));

        // D'abord on range les articles dans « Autres », ensuite seulement on supprime
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("categoryId").is(id)),
                new Update().set("categoryId", fallback.getId())
                        .set("categoryName", fallback.getName()),
                Article.class);

        categoryRepository.delete(category);
    }

    private Category getOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Catégorie introuvable"));
    }

    private void ensureNotDefault(Category category, String action) {
        if (CategoryInitializer.DEFAULT_KEY.equals(category.getNameKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La catégorie par défaut « " + CategoryInitializer.DEFAULT_NAME
                            + " » ne peut pas être " + action);
        }
    }

    // « "  Sport   " » devient « Sport »
    private String cleanName(String raw) {
        return raw.trim().replaceAll("\\s+", " ");
    }

    private List<String> cleanKeywords(List<String> raw) {
        if (raw == null) {
            return List.of();
        }
        return raw.stream()
                .filter(Objects::nonNull)
                .map(k -> k.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT))
                .filter(k -> !k.isEmpty())
                .distinct()
                .toList();
    }

    private ResponseStatusException alreadyExists() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT, "Cette catégorie existe déjà");
    }
}