package com.example.CityPolling.service;

import com.example.CityPolling.dto.LoginRequest;
import com.example.CityPolling.dto.LoginResponse;
import com.example.CityPolling.dto.RegisterRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        String hashed = passwordEncoder.encode(req.getPassword());
        user.setPassword(hashed);
        user.setCity(req.getCity());
        user.setRole("USER");
        return userRepository.save(user);
    }

    // Needed when login needs to be done
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        // If password is correct and matches stored password
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getId());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
