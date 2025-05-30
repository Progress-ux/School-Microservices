package com.progress.school.controller;

import com.progress.school.dto.CreateRequest;
import com.progress.school.model.School;
import com.progress.school.repository.SchoolRepository;
import com.progress.school.repository.SchoolTeacherRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Данные о школах",
        description = "Операции с образовательными учреждениями")
public class SchoolController {
    private final SchoolRepository schoolRepository;
    private final SchoolTeacherRepository schoolTeacherRepository;
    private final SchoolService schoolService;
    private final ValidateTokenService validateTokenService;

    public SchoolController(
            SchoolRepository schoolRepository,
            SchoolService schoolService,
            ValidateTokenService validateTokenService,
            SchoolTeacherRepository schoolTeacherRepository) {
        this.schoolRepository = schoolRepository;
        this.schoolTeacherRepository = schoolTeacherRepository;
        this.schoolService = schoolService;
        this.validateTokenService = validateTokenService;
    }

    private Map<String, Object> extractUser(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) return null;
        return validateTokenService.getUserInfo(header);
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
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");
        if ("ADMIN".equals(role))
        {
            schoolService.createSchool(request);
            return ResponseEntity.ok("Школа добавлена");
        }
        else
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }
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

    @PutMapping("/schools/{id}")
    @Operation(
            summary = "Обновление данных школы",
            description = "Доступно только администратору",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> updateIdSchool(@PathVariable(name = "id") Long id,
                                            @RequestBody CreateRequest request,
                                            HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if(!"ADMIN".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        if (!schoolRepository.existsById(id))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Школа не найдена");
        }

        schoolService.updateSchool(id, request);
        return ResponseEntity.ok("Данные школы обновлены");
    }

    @DeleteMapping("/schools/{id}")
    public ResponseEntity<?> deleteIdSchool(@PathVariable(name = "id") Long id,
                                            HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if(!"ADMIN".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Школа не найдена"));

        schoolRepository.delete(school);
        return ResponseEntity.ok("Школа удалена");
    }

    @GetMapping("/schools/{id}/teachers")
    @Operation(
        summary = "Получение списка учителей в школе",
        description = "Возвращает список ID учителей, прикреплённых к указанной школе",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список учителей получен"),
        @ApiResponse(responseCode = "401", description = "Отсутствует или недействительный токен"),
        @ApiResponse(responseCode = "404", description = "Школа с таким ID не найдена")
    })
    public ResponseEntity<?> getAllTeachersInSchoolId(@PathVariable(name = "id") Long id,
                                                      HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        return ResponseEntity.ok(schoolTeacherRepository.findTeacherInfoBySchoolId(id));
    }

    @GetMapping("/schools/validate")
    public ResponseEntity<?> validateSchoolId(@RequestParam Long id)
    {
        return ResponseEntity.ok(schoolRepository.existsById(id));
    }
}
