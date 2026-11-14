package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    boolean existsByNameKey(String nameKey);

    // Pour la modification : une autre catégorie que celle-ci porte-t-elle déjà ce nom ?
    boolean existsByNameKeyAndIdNot(String nameKey, String id);

    Optional<Category> findByNameKey(String nameKey);
}