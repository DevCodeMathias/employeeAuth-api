package com.devBackend.employAuth.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;

import com.devBackend.employAuth.domain.entity.Employees;
import com.devBackend.employAuth.domain.types.AuthorizationType;
import com.devBackend.employAuth.infrastructure.model.Envelope;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "test-secret-value-with-at-least-32-chars";

    @Test
    void generateTokenIncludesEmployeeIdAuthorizationTypeAndExpiration() {
        JwtService jwtService = new JwtService(new JwtProperties(SECRET, 3));
        Envelope<Employees> employeeEnvelope = new Envelope<>("employee-id", null, null,
                new Employees("Maria", "52998224725", "encoded-password"));

        String token = jwtService.generateToken(employeeEnvelope);

        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("employee-id", claims.getSubject());
        assertEquals("employee-id", claims.get("employeeId", String.class));
        assertEquals(AuthorizationType.EMPLOYEE.name(), claims.get("authorizationType", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertEquals(10800, jwtService.expiresInSeconds());
    }
}
