package com.example.CityPolling.controller;

import com.example.CityPolling.dto.LoginRequest;
import com.example.CityPolling.dto.LoginResponse;
import com.example.CityPolling.dto.RegisterRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.security.JwtUtil;
import com.example.CityPolling.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User savedUser = userService.register(req);
        if(savedUser == null) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }
        savedUser.setPassword(null);
        return ResponseEntity.ok(savedUser);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        boolean valid = userService.validateCredentials(req.getEmail(), req.getPassword());
        if (!valid) {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }

        // Create token (we use email as subject/username)
        String token = jwtUtil.generateToken(req.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());
        Long userId = userOpt.map(User::getId).orElse(null);

        LoginResponse resp = new LoginResponse(token, req.getEmail(), userId);
        return ResponseEntity.ok(resp);
    }

//    @GetMapping
//    public List<User> getAllUsers() {
//        return userService.getAllUsers();
//    }
}
