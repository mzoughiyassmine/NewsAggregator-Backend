package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.PasswordResetCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends MongoRepository<PasswordResetCode, String> {

    Optional<PasswordResetCode> findByEmail(String email);
    void deleteByEmail(String email);
}