package com.progress.timetable.controller;

import com.progress.timetable.dto.CreateRequest;
import com.progress.timetable.model.Timetable;
import com.progress.timetable.repository.TimetableBookRepository;
import com.progress.timetable.repository.TimetableRepository;
import com.progress.timetable.service.TimetableService;
import com.progress.timetable.service.ValidateSchoolTeacherService;
import com.progress.timetable.service.ValidateTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Данные о Расписании",
        description = "Операции с расписанием")
public class TimetableController {
    private final ValidateTokenService validateTokenService;
    private final ValidateSchoolTeacherService validateSchoolTeacherService;
    private final TimetableService timetableService;
    private final TimetableRepository timetableRepository;
    private final TimetableBookRepository timetableBookRepository;

    public TimetableController(ValidateTokenService validateTokenService,
                               TimetableService timetableService,
                               ValidateSchoolTeacherService validateSchoolTeacherService,
                               TimetableRepository timetableRepository,
                               TimetableBookRepository timetableBookRepository) {
        this.validateTokenService = validateTokenService;
        this.timetableService = timetableService;
        this.validateSchoolTeacherService = validateSchoolTeacherService;
        this.timetableRepository = timetableRepository;
        this.timetableBookRepository = timetableBookRepository;
    }

    private Map<String, Object> extractUser(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) return null;
        return validateTokenService.getUserInfo(header);
    }

    @PostMapping("/timetables")
    @Operation(summary = "Создание расписания",
        description = "Создаёт новое расписание, доступно только для учителя и ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Расписание добавлено"),
        @ApiResponse(responseCode = "401", description = "Невалидный или отсутствующий токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Школа или учитель не найдены")
    })
    public ResponseEntity<String> createTimetable(
                @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные для создания расписания")
                @RequestBody CreateRequest request,
                HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if("STUDENT".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        if(!validateSchoolTeacherService.isValidateTeacher(request.getSchoolId(), request.getTeacherId()))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Школа или учитель не найдены");
        }

        timetableService.createTimetable(request);
        return ResponseEntity.ok("Расписание добавлено");
    }

    @GetMapping("/timetables")
    @Operation(summary = "Получение всех расписаний",
            description = "Возвращает список всех расписаний.")
    public ResponseEntity<?> getAllTimetable()
    {
        return ResponseEntity.ok(timetableRepository.findAll());
    }

    @GetMapping("/timetables/{id}")
    @Operation(summary = "Получение расписания по ID", description = "Возвращает одно расписание по его ID.")
    public ResponseEntity<?> getTimetableById(@Parameter(description = "ID расписания", required = true)
                                                  @PathVariable("id") Long id)
    {
        return ResponseEntity.ok(timetableRepository.findById(id));
    }

    @PutMapping("/timetables/{id}")
    @Operation(summary = "Обновление расписания",
        description = "Обновляет расписание по ID. Доступно для TEACHER и ADMIN.")
    public ResponseEntity<?> updateTimetableById(@Parameter(description = "ID расписания", required = true)
                                                 @PathVariable("id") Long id,
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                 description = "Обновлённые данные", required = true)
                                                 @RequestBody CreateRequest request,
                                                 HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if("STUDENT".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        timetableService.updateTimetable(id, request);
        return ResponseEntity.ok("Данные школы обновлены");
    }

    @DeleteMapping("/timetables/{id}")
    @Operation(summary = "Удаление расписания",
        description = "Удаляет расписание по ID. Только для TEACHER и ADMIN.")
    public ResponseEntity<?> deleteTimetableById(@Parameter(description = "ID расписания", required = true)
                                                     @PathVariable("id") Long id,
                                                 HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if("STUDENT".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Расписание не найдено"));

        timetableRepository.delete(timetable);
        return ResponseEntity.ok("Расписание удалено");
    }

    @PostMapping("/timetables/{id}/book")
    @Operation(summary = "Запись ученика на расписание",
        description = "Позволяет STUDENT записаться на занятие по указанному ID.")
    public ResponseEntity<String> createTimetableBook(@Parameter(description = "ID расписания", required = true)
                                                          @PathVariable("id") Long timetable_id,
                                                        HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if(!"STUDENT".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступно только ученикам");
        }

        if(!timetableRepository.existsById(timetable_id))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Расписание не найдено");
        }

        Long student_id = ((Number) userInfo.get("id")).longValue();
        timetableService.createTimetableBook(timetable_id, student_id);
        return ResponseEntity.ok("Запись на занятие сохранена");
    }

    @GetMapping("/timetables/validate")
    @Operation(summary = "Валидация расписания", description = "Проверка наличия расписания по ID.")
    public ResponseEntity<?> validateTimetableById(@Parameter(description = "ID расписания", required = true)
                                                       @RequestParam(name = "id") Long id)
    {
        return ResponseEntity.ok(timetableRepository.existsById(id));
    }

    @GetMapping("/timetables/school/{schoolId}")
    @Operation(summary = "Получение расписаний по ID школы",
        description = "Возвращает все расписания, связанные с указанной школой. Требуется авторизация.")
    public ResponseEntity<?> getTimetableBySchoolId(@Parameter(description = "ID школы", required = true)
                                                        @PathVariable("schoolId") Long id,
                                                    HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");


        return ResponseEntity.ok(timetableRepository.findAllBySchoolId(id));
    }

    @GetMapping("/timetables/teacher/{teacherId}")
    @Operation(summary = "Получение расписаний по ID учителя",
        description = "Возвращает все расписания учителя. Доступно только для TEACHER и ADMIN.")
    public ResponseEntity<?> getTimetableByTeacherId(@Parameter(description = "ID учителя", required = true)
                                                         @PathVariable("teacherId") Long id,
                                                     HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if("STUDENT".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        // "использует Account сервис для проверки учителя"
        // А что он должен проверять?
        return ResponseEntity.ok(timetableRepository.findAllByTeacherId(id));
    }

}
