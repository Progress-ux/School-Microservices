package com.progress.school.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ValidateTokenService {
    private final WebClient webClient;

    public ValidateTokenService(WebClient.Builder webClientBuilder)
    {
        this.webClient = webClientBuilder.baseUrl("http://account-service:8080").build();
    }

    public Mono<Map<String, Object>> getUserInfo(String token)
    {
        return webClient.get()
                .uri("/api/v1/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorResume(e -> Mono.empty());
    }
}
