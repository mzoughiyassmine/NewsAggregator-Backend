package com.example.news_agg_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        // Secret factice, mais encodé en Base64 comme JwtService l'attend
        "app.jwt.secret=dGVzdC1zZWNyZXQtZGUtMzItb2N0ZXRzLW1pbmltdW0tMDEyMzQ1Njc4OQ==",
        "app.admin.password=mot-de-passe-de-test",
        // Base séparée : le test ne touche pas à news_aggregator
        "spring.mongodb.uri=mongodb://localhost:27017/news_aggregator_test",
        // Pas de collecte automatique pendant le test
        "app.collector.initial-delay-ms=3600000"
})
class NewsAggBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}