package com.example.CityPolling.controller;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // Get current logged-in user's info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if(authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = userOpt.get();
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // Update logged-in user's city
    @PutMapping("/update-city")
    public ResponseEntity<?> updateCity(@RequestBody CityRequest req, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()){
            return ResponseEntity.status(404).body("User not found");
        }
        User user = userOpt.get();
        user.setCity(req.getCity());
        userService.save(user);
        return ResponseEntity.ok("City updated successfully to: " + user.getCity());
    }
}
