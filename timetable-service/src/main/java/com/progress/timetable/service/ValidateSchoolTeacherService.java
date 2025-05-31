package com.progress.timetable.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ValidateSchoolTeacherService {
    private final RestTemplate restTemplate;

    public ValidateSchoolTeacherService()
    {
        this.restTemplate = new RestTemplate();
    }

    public boolean isValidateSchool(Long school_id)
    {
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    "http://school-service:8081/api/v1/schools/validate?id=" + school_id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Boolean>() {
                    }
            );
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            System.out.println("Ошибка валидации школы: " + e.getMessage());
            return false;
        }
    }
    public boolean isValidateTeacher(Long school_id, Long teacher_id)
    {
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    "http://school-service:8081/api/v1/schools/" + school_id + "/validate-teacher/" + teacher_id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Boolean>() {
                    }
            );
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            System.out.println("Ошибка валидации учителя: " + e.getMessage());
            return false;
        }
    }
}
