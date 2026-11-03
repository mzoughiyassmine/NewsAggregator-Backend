package com.example.news_agg_backend.dto;

import com.example.news_agg_backend.model.Role;

public record LoginResponse(
        String token,
        String name,
        String email,
        Role role
) {
}