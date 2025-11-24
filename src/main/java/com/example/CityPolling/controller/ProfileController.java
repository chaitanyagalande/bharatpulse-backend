package com.example.CityPolling.controller;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.dto.PasswordUpdateRequest;
import com.example.CityPolling.dto.UsernameUpdateRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        String email = authentication.getName();
        User user = profileService.getCurrentUser(email);
        // No need to check if user is present as this is an authentication request and user is present obviously
        return ResponseEntity.ok(user);
    }

    // Update logged-in user's city
    @PutMapping("/update-city")
    public ResponseEntity<?> updateCity(@Valid @RequestBody CityRequest req, Authentication authentication) {
        String email = authentication.getName();
        User updatedUser = profileService.updateCity(req, email);
        return ResponseEntity.ok("City updated successfully to: " + updatedUser.getCity());
    }

    // Change password after logging in
    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateRequest req, Authentication authentication) {
        String email = authentication.getName();
        boolean updated = profileService.updatePassword(req, email);
        if(!updated) {
            return ResponseEntity.status(400).body("Old password is incorrect.");
        }
        return ResponseEntity.ok("Password updated successfully!");
    }

    // Change username after logging in
    @PatchMapping("/update-username")
    public ResponseEntity<?> updateUsername(@Valid @RequestBody UsernameUpdateRequest req, Authentication authentication) {
        String email = authentication.getName();
        boolean updated = profileService.updateUsername(req, email);
        if(!updated) {
            return ResponseEntity.status(400).body("Username already taken.");
        }
        return ResponseEntity.ok("Username updated successfully!");
    }

    // Toggle Mode between LOCAL and EXPLORER
    @PatchMapping("/toggle-mode")
    public ResponseEntity<?> toggleMode(Authentication authentication) {
        String email = authentication.getName();
        String mode = profileService.toggleMode(email);
        return ResponseEntity.ok(mode);
    }

    // Delete own account also remove polls created and votes registered
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        String email = authentication.getName();
        profileService.deleteUserAccount(email);
        return ResponseEntity.ok("User account and related data deleted successfully.");
    }
}
