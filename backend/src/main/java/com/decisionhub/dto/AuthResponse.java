package com.decisionhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String tokenType;
    private Long expiresIn;

    public static AuthResponse of(String token, String username, String role, Long expiresIn) {
        return AuthResponse.builder()
                .token(token)
                .username(username)
                .role(role)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}