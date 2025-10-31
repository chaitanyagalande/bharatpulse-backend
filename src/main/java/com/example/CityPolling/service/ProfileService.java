package com.example.CityPolling.service;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.dto.PasswordUpdateRequest;
import com.example.CityPolling.dto.UsernameUpdateRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty())
            return ResponseEntity.status(404).body("User not found");

        User user = userOpt.get();
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> updateCity(CityRequest request, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("User not found");

        User user = userOpt.get();
        user.setCity(request.getCity());
        userRepository.save(user);

        return ResponseEntity.ok("City updated successfully to: " + user.getCity());
    }

    public ResponseEntity<?> updatePassword(PasswordUpdateRequest req, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("User not found");

        User user = userOpt.get();

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password updated successfully!");
    }

    public ResponseEntity<?> updateUsername(UsernameUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("User not found");

        User user = userOpt.get();

        if (userRepository.existsByName(request.getNewUsername())) {
            return ResponseEntity.status(400).body("Username already taken.");
        }

        user.setName(request.getNewUsername());
        userRepository.save(user);
        return ResponseEntity.ok("Username updated successfully!");
    }

}
