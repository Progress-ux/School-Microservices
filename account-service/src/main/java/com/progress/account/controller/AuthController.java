package com.progress.account.controller;

import com.progress.account.dto.AuthResponse;
import com.progress.account.dto.LoginRequest;
import com.progress.account.dto.RegisterRequest;
import com.progress.account.model.User;
import com.progress.account.repository.UserRepository;
import com.progress.account.security.JwtUtil;
import com.progress.account.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Аутентификация", description = "Операции регистрации, входа, получения и валидации пользователей")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil,
                          AuthService authService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя",
    security = @SecurityRequirement(name = ""))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации или пользователь уже существует")
    })
    public ResponseEntity<String> register(@RequestBody RegisterRequest request)
    {
        try {
            authService.register(request);
            return ResponseEntity.ok("Пользователь зарегистрирован");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя",
            description = "Возвращает JWT токен при успешной аутентификации",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "401", description = "Неверный пароль или пользователь не найден")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request)
    {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword_hash()))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/user")
    @Operation(summary = "Получение информации о текущем пользователе",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Невалидный или отсутствующий токен")
    })
    public ResponseEntity<?> getCurrentUser(HttpServletRequest httpServletRequest)
    {
        try {
            String header = httpServletRequest.getHeader("Authorization");

            if(header == null || !header.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            String token = header.substring(7);

            if(jwtUtil.isTokenExpired(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", jwtUtil.extractId(token));
            userInfo.put("email", jwtUtil.extractEmail(token));
            userInfo.put("role", jwtUtil.extractRole(token));

            return ResponseEntity.ok(userInfo);

        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный токен");
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Проверка валидности токена",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен валиден"),
            @ApiResponse(responseCode = "401", description = "Токен невалиден или истёк")
    })
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String header)
    {
        if(header == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
        }

        String token = header.startsWith("Bearer ") ? header.substring(7) : header;

        try {
            if(jwtUtil.isTokenExpired(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

            Long id = jwtUtil.extractId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("id", id);
            response.put("email", email);
            response.put("role", role);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный токен");
        }
    }

    @PutMapping("/user")
    @Operation(summary = "Обновление данных пользователя",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Нет токена")
    })
    public ResponseEntity<?> updateDataUser(@RequestBody RegisterRequest request, HttpServletRequest httpServletRequest)
    {
        try {
            String header = httpServletRequest.getHeader("Authorization");
            if(header == null || !header.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            String token = header.substring(7);

            if(jwtUtil.isTokenExpired(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

            authService.updateUser(token, request);
            return ResponseEntity.ok("Информация обновлена");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user")
    @Operation(summary = "Удаление пользователя",
               description = "Удаляет пользователя. Доступно только администраторам.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "401", description = "Нет токена или истёк"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<?> deleteUser(@RequestBody Long id, HttpServletRequest httpServletRequest)
    {
        try {
            String header = httpServletRequest.getHeader("Authorization");
            if(header == null || !header.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            String token = header.substring(7);

            if(jwtUtil.isTokenExpired(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            userRepository.delete(user);
            return ResponseEntity.ok("Пользователь удален");
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении пользователя");
        }
    }

    @GetMapping("/users")
    @Operation(summary = "Получение списка всех пользователей",
               description = "Возвращает информацию о пользователях. Доступно только администраторам.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "401", description = "Нет токена или истёк"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
    })
    public ResponseEntity<?> getAllUsers(HttpServletRequest httpServletRequest)
    {
        String header = httpServletRequest.getHeader("Authorization");
            if(header == null || !header.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            if(jwtUtil.isTokenExpired(header.substring(7)))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/user/{id}")
    @Operation(
            summary = "Получение пользователя по ID",
            description = "Возвращает информацию о пользователе по ID. Доступно только администраторам.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Отсутствует токен или токен истёк"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным ID не найден")
    })
    public ResponseEntity<?> getUserById(@Parameter(description = "ID пользователя")
                                             @PathVariable(name = "id") Long id,
                                         HttpServletRequest httpServletRequest)
    {

        String header = httpServletRequest.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            if(jwtUtil.isTokenExpired(header.substring(7)))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", id);
        userInfo.put("email", user.getEmail());
        userInfo.put("firstName", user.getFirst_name());
        userInfo.put("lastName", user.getLast_name());
        userInfo.put("role", user.getRole());

        return ResponseEntity.ok(userInfo);
    }
}
