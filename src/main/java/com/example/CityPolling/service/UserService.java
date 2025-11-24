package com.example.CityPolling.service;

import com.example.CityPolling.dto.LoginRequest;
import com.example.CityPolling.dto.LoginResponse;
import com.example.CityPolling.dto.RegisterRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegisterRequest req) {
        String normalizedUsername = req.getUsername().toLowerCase().trim();
        String normalizedEmail = req.getEmail().toLowerCase().trim();
        String normalizedCity = req.getCity().toLowerCase().trim();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        String hashed = passwordEncoder.encode(req.getPassword());
        user.setPassword(hashed);
        user.setCity(normalizedCity);
        user.setRole("USER");
        return userRepository.save(user);
    }

    // Needed when login needs to be done
    public LoginResponse login(LoginRequest req) {
        // ✅ Normalize email to lowercase for lookup
        String normalizedEmail = req.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail);
        if(user == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        // If password is correct and matches stored password
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getId());
    }

    public boolean existsByEmail(String email) {
        // ✅ Normalize email to lowercase for check
        return userRepository.existsByEmail(email.toLowerCase().trim());
    }

    public User findByEmail(String email) {
        // ✅ Normalize email to lowercase for lookup
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    public User findById(Long createdBy) {
        return userRepository.findById(createdBy)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdBy));
    }

}
