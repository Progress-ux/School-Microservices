package com.progress.account.controller;

import com.progress.account.dto.AuthResponse;
import com.progress.account.dto.LoginRequest;
import com.progress.account.dto.RegisterRequest;
import com.progress.account.model.User;
import com.progress.account.repository.UserRepository;
import com.progress.account.security.JwtUtil;
import com.progress.account.service.AuthService;
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
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader)
    {
        try {
            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            String token = authorizationHeader.substring(7);

            if(jwtUtil.isTokenExpired(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirst_name());
            userInfo.put("lastName", user.getLast_name());
            userInfo.put("role", user.getRole());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный токен");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader)
    {
        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
        }

        String token = authHeader.substring(7);
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
    public ResponseEntity<?> updateDataUser(@RequestBody RegisterRequest request, HttpServletRequest httpServletRequest)
    {
        try {
            String authHeader = httpServletRequest.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            String token = authHeader.substring(7);
            authService.updateUser(token, request);
            return ResponseEntity.ok("Информация обновлена");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader)
    {
        try {
            if(authHeader == null || !authHeader.startsWith("Bearer "))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Отсутствует токен");
            }

            String token = authHeader.substring(7);

            if (jwtUtil.isTokenExpired(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен истек");
            }

            Long id = jwtUtil.extractId(token);
            String role = jwtUtil.extractRole(token);

            if(!role.equals("ADMIN"))
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещён: требуется роль ADMIN");
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
}
