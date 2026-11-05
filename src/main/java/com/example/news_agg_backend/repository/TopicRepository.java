package com.example.news_agg_backend.repository;

import com.example.news_agg_backend.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TopicRepository extends MongoRepository<Topic, String> {

    List<Topic> findByCodeIn(List<String> codes);
}
