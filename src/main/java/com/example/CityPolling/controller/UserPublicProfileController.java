package com.example.CityPolling.controller;

import com.example.CityPolling.dto.PollWithVoteResponse;
import com.example.CityPolling.dto.UserPublicProfileResponse;
import com.example.CityPolling.service.UserPublicProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserPublicProfileController {
    private final UserPublicProfileService userPublicProfileService;

    public UserPublicProfileController(UserPublicProfileService userPublicProfileService) {
        this.userPublicProfileService = userPublicProfileService;
    }

    // Get user's public profile
    @GetMapping("/{username}")
    public ResponseEntity<UserPublicProfileResponse> getUserProfile(@PathVariable String username) {
        UserPublicProfileResponse profile = userPublicProfileService.getUserPublicProfile(username);
        return ResponseEntity.ok(profile);
    }

    // Get user's created polls
    @GetMapping("/{userId}/polls-created")
    public ResponseEntity<List<PollWithVoteResponse>> getPollsCreatedByUser(@PathVariable Long userId, @RequestParam(defaultValue = "latest") String sortBy) {
        List<PollWithVoteResponse> polls = userPublicProfileService.getPollsCreatedByUser(userId, sortBy);
        return ResponseEntity.ok(polls);
    }
    // Get user's voted polls
    @GetMapping("/{userId}/polls-voted")
    public ResponseEntity<List<PollWithVoteResponse>> getPollsVotedByUser(@PathVariable Long userId, @RequestParam(defaultValue = "latest") String sortBy) {
        List<PollWithVoteResponse> polls = userPublicProfileService.getPollsVotedByUser(userId, sortBy);
        return ResponseEntity.ok(polls);
    }
}
