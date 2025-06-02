package com.progress.account.security;

import java.io.IOException;

import com.progress.account.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

/**
 * JWT фильтр, который проверяет авторизацию пользователя на каждом HTTP-запросе.
 * Если в заголовке Authorization есть валидный JWT токен, извлекает из него email и роль,
 * и вручную устанавливает аутентификацию в SecurityContext.
 */
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;


    public JwtAuthFilter(JwtUtil jwtUtil)
    {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
    throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = authHeader.substring(7);

            try {

                if (!jwtUtil.isTokenExpired(token)) {
                    String email = jwtUtil.extractEmail(token);
                    String roleStr = jwtUtil.extractRole(token);
                    Role role = Role.valueOf(roleStr);

                    // Создаем список прав на основе роли
                    List<GrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority(role.toString()));

                    // Создаём объект аутентификации
                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    // Устанавливаем аутентификацию в контекст безопасности
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
