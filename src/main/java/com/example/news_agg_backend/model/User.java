
package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private Role role;

    private AccountStatus status;

    @Builder.Default
    private boolean emailVerified = false;

    @Builder.Default
    private List<String> topicCodes = List.of();

    @Builder.Default
    private String language = "fr";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
