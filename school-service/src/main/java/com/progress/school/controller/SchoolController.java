package com.progress.school.controller;

import com.progress.school.dto.CreateRequest;
import com.progress.school.dto.StudentInfo;
import com.progress.school.dto.TeacherInfo;
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
import java.util.List;
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
    @Operation(summary = "Создание новой школы",
            description = "Позволяет создать новую школу. Требуются права администратора.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Школа успешно добавлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка при добавлении школы"),
            @ApiResponse(responseCode = "401", description = "Невалидный или отсутствующий токен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> createSchool(@RequestBody CreateRequest request,
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

        schoolService.createSchool(request);
        return ResponseEntity.ok("Школа добавлена");
    }


    @GetMapping("/schools")
    @Operation(
        summary = "Получение всех школ",
        description = "Возвращает список всех школ"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список школы получен"),
            @ApiResponse(responseCode = "404", description = "Не удалось получить список школ")
    })
    public ResponseEntity<?> getAllSchools()
    {
        return ResponseEntity.ok(schoolRepository.findAll());
    }

    @GetMapping("/schools/{id}")
    @Operation(
        summary = "Получение школы по ID",
        description = "Возвращает информацию о школе по ее ID"
    )
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
        description = "Обновляет информацию о школе. Доступно только для администраторов.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Данные школы обновлены"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "401", description = "Невалидный или отсутствующий токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Школа не найдена")
    })
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
    @Operation(
        summary = "Удаление школы",
        description = "Удаляет школу по ее ID. Требуются права администратора."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Школа удалена"),
        @ApiResponse(responseCode = "401", description = "Невалидный или отсутствующий токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Школа не найдена")
    })
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
        description = "Возвращает список учителей, прикреплённых к указанной школе",
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

        List<TeacherInfo> teachers = schoolTeacherRepository.findTeacherInfoBySchoolId(id);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/schools/validate")
    @Operation(
        summary = "Проверка существования школы",
        description = "Возвращает true или false, зависит от наличия школы по указанному ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ответ получен"),
        @ApiResponse(responseCode = "401", description = "Отсутствует или недействительный токен"),
        @ApiResponse(responseCode = "404", description = "Школа с таким ID не найдена")
    })
    public ResponseEntity<?> validateSchoolId(@RequestParam("id") Long id)
    {
        return ResponseEntity.ok(schoolRepository.existsById(id));
    }

    @GetMapping("/schools/{id}/students")
    @Operation(
        summary = "Получение списка студентов в школе",
        description = "Возвращает список студентов, прикреплённых к указанной школе",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список студентов получен"),
        @ApiResponse(responseCode = "401", description = "Отсутствует или недействительный токен"),
        @ApiResponse(responseCode = "404", description = "Школа с таким ID не найдена")
    })
    public ResponseEntity<?> getAllStudentsInSchoolId(@PathVariable(name = "id") Long id,
                                                      HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if(role.equals("STUDENT"))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        List<StudentInfo> students = schoolTeacherRepository.findStudentInfoBySchoolId(id);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/schools/{id}/validate-teacher/{teacherId}")
    @Operation(
        summary = "Проверка существования учителя в школе",
        description = "Возвращает true или false, зависит от наличия школы по указанному ID и учителя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ответ получен"),
        @ApiResponse(responseCode = "401", description = "Отсутствует или недействительный токен"),
        @ApiResponse(responseCode = "404", description = "Школа с таким ID не найдена")
    })
    public ResponseEntity<?> validateTeacher(@PathVariable("id") Long school_id,
                                             @PathVariable("teacherId") Long teacher_id)
    {
        return ResponseEntity.ok(schoolTeacherRepository.existsBySchoolIdAndTeacherId(school_id, teacher_id));
    }
}
