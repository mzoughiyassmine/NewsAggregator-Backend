package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.EmailVerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends MongoRepository<EmailVerificationCode, String> {
    Optional<EmailVerificationCode> findByEmail(String email);
    void deleteByEmail(String email);
}
