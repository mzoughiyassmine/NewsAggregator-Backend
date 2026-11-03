package com.example.news_agg_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminTestController {

    @GetMapping("/api/admin/ping")
    public String ping() {
        return "Accès administrateur autorisé";
    }
}