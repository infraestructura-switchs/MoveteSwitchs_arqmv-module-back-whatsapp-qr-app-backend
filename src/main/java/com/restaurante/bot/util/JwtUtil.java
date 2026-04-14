package com.restaurante.bot.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private long EXPIRATION_TIME;

    // Genera un token con externalCompanyId como claim
    public String generateToken(Long externalCompanyId, Long userId) {
        return generateToken(externalCompanyId, userId, generateSessionId());
    }

    public String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String generateToken(Long externalCompanyId, Long userId, String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("externalCompanyId", externalCompanyId);
        claims.put("userId", userId);
        claims.put("sessionId", sessionId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extrae un claim específico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .setAllowedClockSkewSeconds(60) // allow small clock skew
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // Extrae externalCompanyId del token
    public Long extractExternalCompanyId(String token) {
        Long companyId = extractClaim(token, claims -> claims.get("externalCompanyId", Long.class));
        if (companyId == null) {
            log.warn("Token missing required claim: externalCompanyId");
            throw new JwtException("Token must contain externalCompanyId claim");
        }
        return companyId;
    }

    public String extractSessionId(String token) {
        String sessionId = extractClaim(token, claims -> claims.get("sessionId", String.class));
        if (sessionId == null || sessionId.isEmpty()) {
            log.warn("Token missing or empty sessionId claim");
            throw new JwtException("Token must contain valid sessionId claim");
        }
        return sessionId;
    }

    public Long extractUserId(String token) {
        Long userId = extractClaim(token, claims -> claims.get("userId", Long.class));
        if (userId == null) {
            log.warn("Token missing required claim: userId");
            throw new JwtException("Token must contain userId claim");
        }
        return userId;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Valida si el token es válido (no expirado y firmado correctamente)
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}