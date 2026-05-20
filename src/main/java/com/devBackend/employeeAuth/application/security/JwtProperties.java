package com.devBackend.employeeAuth.application.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.secret")
public record JwtProperties(
        String secret,
        long expirationHours
) {
}
