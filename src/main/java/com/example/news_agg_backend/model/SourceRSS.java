package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "rss_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceRSS {

    @Id
    private String id;

    private String name;

    // Adresse du flux RSS, par exemple https://exemple.com/rss
    private String url;

    private String description;

    private SourceStatus status;

    private LocalDateTime createdAt;
}