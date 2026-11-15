// VerifyRegistrationCodeRequest.java
package com.example.news_agg_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyRegistrationCodeRequest(
        @NotBlank @Email String email,
        @NotBlank String code
) {}