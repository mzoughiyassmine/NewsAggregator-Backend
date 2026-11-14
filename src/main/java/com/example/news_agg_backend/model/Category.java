package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    private String id;

    // Nom saisi par l'admin (« Sport », « Économie »…) : c'est lui qu'on affiche
    private String name;

    // Version en minuscules du nom, utilisée uniquement pour détecter les doublons.
    @Indexed(unique = true)
    private String nameKey;

    // Mots-clés qui servent à classer automatiquement les articles (US14).
    // Peut être nul pour les catégories créées avant cette évolution.
    private List<String> keywords;

    private LocalDateTime createdAt;
}