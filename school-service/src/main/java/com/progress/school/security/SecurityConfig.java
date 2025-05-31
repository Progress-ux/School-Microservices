package com.progress.school.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/schools").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/schools").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/schools/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/v1/schools/*").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/schools/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/schools/*/teachers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/schools/validate").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/schools/*/students").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/schools/*/validate-teacher/*").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

