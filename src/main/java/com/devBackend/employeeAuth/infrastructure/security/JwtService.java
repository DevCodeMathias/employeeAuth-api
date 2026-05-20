package com.devBackend.employeeAuth.infrastructure.security;

import com.devBackend.employeeAuth.application.interfaces.IJwtService;
import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.domain.types.AuthorizationType;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService implements IJwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateToken(Envelope<Employees> employeeEnvelope) {

        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.expirationHours(), ChronoUnit.HOURS);
        log.debug("Generating JWT for employeeId={} expiresAt={}", employeeEnvelope.getId(), expiresAt);

        return Jwts.builder()
                .subject(employeeEnvelope.getId())
                .claim("employeeId", employeeEnvelope.getId())
                .claim("authorizationType", AuthorizationType.EMPLOYEE.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public long expiresInSeconds() {
        return properties.expirationHours() * 60 * 60;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
