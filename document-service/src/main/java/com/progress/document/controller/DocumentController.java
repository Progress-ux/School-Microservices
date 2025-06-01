package com.progress.document.controller;

import com.progress.document.dto.CreateRequest;
import com.progress.document.model.Document;
import com.progress.document.repository.DocumentRepository;
import com.progress.document.service.DocumentService;
import com.progress.document.service.ValidateTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Данные о документах",
        description = "Операции с документами")
public class DocumentController {
    private final ValidateTokenService validateTokenService;
    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    public DocumentController(ValidateTokenService validateTokenService,
                              DocumentService documentService,
                              DocumentRepository documentRepository)
    {
        this.validateTokenService = validateTokenService;
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }
    private Map<String, Object> extractUser(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) return null;
        return validateTokenService.getUserInfo(header);
    }

    @PostMapping("/documents")
    @Operation(summary = "Создание документа о посещении",
        description = "Создает документ. Доступно только преподавателям и администраторам")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Документ сохранен"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<String> createDocument(@RequestBody CreateRequest request,
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

        documentService.createDocument(request);
        return ResponseEntity.ok("Документ сохранен");
    }

    @GetMapping("/documents")
    @Operation(summary = "Получение всех документов",
        description = "Возвращает список всех документов. Доступно только администратору")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список документов"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> getAllDocuments(HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if(!"ADMIN".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }

        return ResponseEntity.ok(documentRepository.findAll());
    }

    @GetMapping("/documents/{id}")
    @Operation(summary = "Получение документа по ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Документ найден"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен")
    })
    public ResponseEntity<?> getDocumentById(@Parameter(description = "ID документа")
                                                 @PathVariable("id") Long id,
                                             HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        return ResponseEntity.ok(documentRepository.findById(id));
    }

    @PutMapping("/documents/{id}")
    @Operation(summary = "Обновление документа",
        description = "Обновляет существующий документ. Доступно преподавателям и администраторам")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Документ обновлен"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> updateDocument(@Parameter(description = "ID документа")
                                                @PathVariable("id") Long id,
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

        documentService.updateDocument(id, request);
        return ResponseEntity.ok("Документ обновлен");
    }

    @DeleteMapping("/documents/{id}")
    @Operation(summary = "Удаление документа",
        description = "Удаляет документ. Доступно только администратору")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Документ удален"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Документ не найден")
    })
    public ResponseEntity<?> deleteDocument(@Parameter(description = "ID документа")
                                                @PathVariable("id") Long id,
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

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Документ не найден"));

        documentRepository.delete(document);

        return ResponseEntity.ok("Документ удален");
    }

    @GetMapping("/documents/user/{userId}")
    @Operation(summary = "Получение документов по ID пользователя",
        description = "Доступно ученикам и преподавателям")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список документов"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> getDocumentByUserId(@Parameter(description = "ID пользователя")
                                                     @PathVariable("userId") Long id,
                                                 HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if("ADMIN".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступно только учителям и ученикам");
        }

        return ResponseEntity.ok(documentRepository.findAllByUserId(id));
    }

    @GetMapping("/documents/school/{schoolId}")
    @Operation(summary = "Получение документов по ID школы",
        description = "Доступно только администратору")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список документов"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> getDocumentBySchoolId(@Parameter(description = "ID школы")
                                                       @PathVariable("schoolId") Long id,
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

        return ResponseEntity.ok(documentRepository.findAllBySchoolId(id));
    }

    @GetMapping("/documents/check-attendance")
    @Operation(summary = "Проверка присутствия ученика",
        description = "Проверяет, присутствовал ли ученик в указанный день. Доступно ученикам и преподавателям")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Результат проверки"),
        @ApiResponse(responseCode = "401", description = "Невалидный токен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<?> checkAttendance(@Parameter(description = "ID ученика")
                                                 @RequestParam("studentId") Long id,
                                             @Parameter(description = "Дата (формат yyyy-MM-dd)")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                @RequestParam("date") LocalDate date,
                                             HttpServletRequest httpServletRequest)
    {
        Map<String, Object> userInfo = extractUser(httpServletRequest);
        if(userInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный или отсутствующий токен");

        String role = (String) userInfo.get("role");

        if("ADMIN".equals(role))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступно только учителям и ученикам");
        }

        Optional<Document> document = documentRepository.findByUserIdAndDate(id, date);

        if(document.isPresent())
        {
            return ResponseEntity.ok(Map.of(
                    "status", document.get().getStatus(),
                    "notes", document.get().getNotes()
            ));
        }
        else
        {
            return ResponseEntity.ok("Записей нет");
        }
    }
}
