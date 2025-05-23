package com.progress.account.service;

import com.progress.account.dto.RegisterRequest;
import com.progress.account.model.User;
import com.progress.account.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword_hash(passwordEncoder.encode(request.getPassword_hash()));

        userRepository.save(user);
    }

}
