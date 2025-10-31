package com.example.CityPolling.controller;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.dto.PasswordUpdateRequest;
import com.example.CityPolling.dto.UsernameUpdateRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.service.ProfileService;
import com.example.CityPolling.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Get current logged-in user's info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return profileService.getCurrentUser(authentication);
    }

    // Update logged-in user's city
    @PutMapping("/update-city")
    public ResponseEntity<?> updateCity(@RequestBody CityRequest req, Authentication authentication) {
        return profileService.updateCity(req, authentication);
    }

    // Change password after logging in
    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest req, Authentication authentication) {
        return profileService.updatePassword(req, authentication);
    }

    // Change username after logging in
    @PatchMapping("/update-username")
    public ResponseEntity<?> updateUsername(@RequestBody UsernameUpdateRequest req, Authentication authentication) {
        return profileService.updateUsername(req, authentication);
    }
}
