package com.example.news_agg_backend.controller;

import com.example.news_agg_backend.dto.ArticlePageResponse;
import com.example.news_agg_backend.model.User;
import com.example.news_agg_backend.repository.UserRepository;
import com.example.news_agg_backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ReaderArticleController {

    private final ArticleService articleService;
    private final UserRepository userRepository;

    @GetMapping("/feed")
    public ArticlePageResponse feed(Authentication auth,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return articleService.findFeed(user.getCategoryIds(), page, size);
    }
}