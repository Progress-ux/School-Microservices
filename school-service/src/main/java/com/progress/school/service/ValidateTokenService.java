package com.progress.school.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ValidateTokenService {
    private final RestTemplate restTemplate;

    public ValidateTokenService()
    {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> getUserInfo(String token)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://account-service:8080/api/v1/auth/validate",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        System.out.println("Response from account-service: " + response.getBody());
        return response.getBody();
    }
}
