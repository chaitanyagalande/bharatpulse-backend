package com.example.CityPolling.controller;

import com.example.CityPolling.dto.UserPublicProfileResponse;
import com.example.CityPolling.service.UserPublicProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class UserPublicProfileController {
    private final UserPublicProfileService userPublicProfileService;

    public UserPublicProfileController(UserPublicProfileService userPublicProfileService) {
        this.userPublicProfileService = userPublicProfileService;
    }

    // Get user's public profile
    @GetMapping("/{userId}")
    public ResponseEntity<UserPublicProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserPublicProfileResponse profile = userPublicProfileService.getUserPublicProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
