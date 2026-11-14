package com.example.news_agg_backend.controller;

import com.example.news_agg_backend.dto.CategoryResponse;
import com.example.news_agg_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Lecture seule, pour l'application mobile : c'est ce qui « propose » les catégories aux lecteurs
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ReaderCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }
}