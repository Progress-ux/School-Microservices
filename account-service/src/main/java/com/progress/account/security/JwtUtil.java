package com.progress.account.security;

import com.progress.account.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.Jwts.*;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Генерация JWT токена на основе ID пользователя, email и роли.
     * @param id ID пользователя
     * @param email Email пользователя
     * @param role Роль пользователя (например, ADMIN, STUDENT и т.д.)
     * @return Сгенерированный JWT токен
     */
    public String generateToken(Long id, String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        claims.put("id", id);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject)
    {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // токен действителен 1 час

        return builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // Метод для извлечения всех claims (полей) из JWT токена
    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Проверка, истёк ли срок действия токена.
     * @param token JWT токен
     * @return true, если токен просрочен
     */
    public boolean isTokenExpired(String token)
    {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Извлечение email (subject) из токена.
     * @param token JWT токен
     * @return Email пользователя
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Извлечение роли пользователя из токена.
     * @param token JWT токен
     * @return Роль в виде строки
     */
    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    /**
     * Извлечение ID пользователя из токена.
     * @param token JWT токен
     * @return ID пользователя
     */
    public Long extractId(String token)
    {
        return ((Number) extractAllClaims(token).get("id")).longValue();
    }
}