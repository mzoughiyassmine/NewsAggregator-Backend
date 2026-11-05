
package com.example.news_agg_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "email_verification_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationCode {

    @Id
    private String id;

    private String email;
    private String codeHash;
    private LocalDateTime expiresAt;
}
