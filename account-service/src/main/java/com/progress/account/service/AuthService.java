package com.progress.account.service;

import com.progress.account.dto.RegisterRequest;
import com.progress.account.model.User;
import com.progress.account.repository.UserRepository;
import com.progress.account.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже используется");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirst_name(request.getFirst_name());
        user.setLast_name(request.getLast_name());
        user.setRole(request.getRole());
        user.setPassword_hash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public void updateUser(String token, RegisterRequest request)
    {
        Long userId = jwtUtil.extractId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFirst_name() != null) user.setFirst_name(request.getFirst_name());
        if (request.getLast_name() != null) user.setLast_name(request.getLast_name());

        if (request.getPassword() != null)
        {
            user.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
    }

}
