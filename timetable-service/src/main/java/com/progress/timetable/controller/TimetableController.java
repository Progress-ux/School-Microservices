package com.progress.timetable.controller;

import com.progress.timetable.dto.CreateRequest;
import com.progress.timetable.model.Timetable;
import com.progress.timetable.repository.TimetableRepository;
import com.progress.timetable.service.TimetableService;
import com.progress.timetable.service.ValidateSchoolTeacherService;
import com.progress.timetable.service.ValidateTokenService;
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

    public TimetableController(ValidateTokenService validateTokenService,
                               TimetableService timetableService,
                               ValidateSchoolTeacherService validateSchoolTeacherService,
                               TimetableRepository timetableRepository) {
        this.validateTokenService = validateTokenService;
        this.timetableService = timetableService;
        this.validateSchoolTeacherService = validateSchoolTeacherService;
        this.timetableRepository = timetableRepository;
    }

    private Map<String, Object> extractUser(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) return null;
        return validateTokenService.getUserInfo(header);
    }

    @PostMapping("/timetables")
    public ResponseEntity<String> createTimetable(@RequestBody CreateRequest request,
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

        if(!validateSchoolTeacherService.isValidateTeacher(request.getSchool_id(), request.getTeacher_id()))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Школа или учитель не найдены");
        }

        timetableService.createTimetable(request);
        return ResponseEntity.ok("Расписание добавлено");
    }

    @GetMapping("/timetables")
    public ResponseEntity<?> getAllTimetable()
    {
        return ResponseEntity.ok(timetableRepository.findAll());
    }

    @GetMapping("/timetables/{id}")
    public ResponseEntity<?> getTimetableById(@PathVariable("id") Long id)
    {
        return ResponseEntity.ok(timetableRepository.findById(id));
    }

    @PutMapping("/timetables/{id}")
    public ResponseEntity<?> updateTimetableById(@PathVariable("id") Long id,
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
    public ResponseEntity<?> deleteTimetableById(@PathVariable("id") Long id,
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

    // TODO: POST /api/v1/timetables/{id}/book - Запись на занятие (требует JWT, доступно Ученик)
}
