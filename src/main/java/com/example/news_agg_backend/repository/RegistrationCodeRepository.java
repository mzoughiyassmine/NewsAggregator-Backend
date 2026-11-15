package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.RegistrationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegistrationCodeRepository extends MongoRepository<RegistrationCode, String> {

    Optional<RegistrationCode> findByEmail(String email);

    void deleteByEmail(String email);
}