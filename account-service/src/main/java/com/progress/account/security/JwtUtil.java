package com.progress.account.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateToken(Long id, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("id", id);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject)
    {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 час

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

//    public boolean isAuthorized(String token, String expectedEmail, String requiredRole)
//    {
//        try {
//            String email = extractEmail(token);
//            String role = extractRole(token);
//            return email.equals(expectedEmail) && role.equals(requiredRole) && !isTokenExpired(token);
//        } catch (Exception e) {
//            return false;
//        }
//    }


    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token)
    {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    public Long extractId(String token)
    {
        return ((Number) extractAllClaims(token).get("id")).longValue();
    }
}