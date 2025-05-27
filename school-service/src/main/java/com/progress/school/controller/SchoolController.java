package com.progress.school.controller;

import com.progress.school.dto.CreateRequest;
import com.progress.school.model.School;
import com.progress.school.repository.SchoolRepository;
import com.progress.school.service.SchoolService;
import com.progress.school.service.ValidateTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Данные о школах",
        description = "Операции с образовательными учреждениями")
public class SchoolController {
    private final SchoolRepository schoolRepository;
    private final SchoolService schoolService;
    private final ValidateTokenService validateTokenService;

    public SchoolController(SchoolRepository schoolRepository,
                         SchoolService schoolService,
                         ValidateTokenService validateTokenService)
    {
        this.schoolRepository = schoolRepository;
        this.schoolService = schoolService;
        this.validateTokenService = validateTokenService;
    }

    @PostMapping("/schools")
    @Operation(summary = "Создание новой школы", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Школа успешно добавлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка при добавлении школы"),
            @ApiResponse(responseCode = "401", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> createSchool(@RequestBody CreateRequest request,
                                               HttpServletRequest httpServletRequest)
    {
        String header = httpServletRequest.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer "))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
        }

        return validateTokenService.getUserInfo(header).map(userInfo -> {
            String role = (String) userInfo.get("role");
            if("ADMIN".equals(role))
            {
                schoolService.createSchool(request);
                return ResponseEntity.ok("Школа добавлена");
            }
            else
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Невалидный токен")).block();
    }

    @GetMapping("/schools")
    @Operation(summary = "Получение всех школ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список школы получен"),
            @ApiResponse(responseCode = "404", description = "Не удалось получить список школ")
    })
    public ResponseEntity<?> getAllSchools()
    {
        return ResponseEntity.ok(schoolRepository.findAll());
    }

    @GetMapping("/schools/{id}")
    @Operation(summary = "Получение школы по ID",
            description = "Возвращает информацию о школе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Школа найдена"),
            @ApiResponse(responseCode = "404", description = "Школа с указанным ID не найдена")
    })
    public ResponseEntity<?> getIdSchool(@PathVariable(name = "id") Long id)
    {
        School school = schoolRepository.findById(id)
                .orElse(null);

        if(school == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Школа не найдена");
        }

        Map<String, Object> schoolInfo = new HashMap<>();
        schoolInfo.put("id", id);
        schoolInfo.put("name", school.getName());
        schoolInfo.put("address", school.getAddress());

        return ResponseEntity.ok(schoolInfo);
    }


}
