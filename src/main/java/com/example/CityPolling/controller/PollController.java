package com.example.CityPolling.controller;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.service.PollService;
import com.example.CityPolling.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollService pollService;
    private final UserService userService;

    public PollController(PollService pollService, UserService userService) {
        this.pollService = pollService;
        this.userService = userService;
    }

    // Create a new poll (JWT protected)
    @PostMapping("/create")
    public ResponseEntity<?> createPoll(@RequestBody Poll poll, Authentication authentication) {
        String email = authentication.getName(); // email stored as name in token
        Poll savedPoll = pollService.createPoll(poll, email);
        return ResponseEntity.ok(savedPoll);
    }

    // Get all polls for the logged-in user's city
    @GetMapping("/mycity")
    public ResponseEntity<?> getPollsForCity(Authentication authentication) {
        String email = authentication.getName();
        List<Poll> polls = pollService.getPollsByCity(email);
        return ResponseEntity.ok(polls);
    }

    // Edit a poll (Only the creator can)
    @PutMapping("/edit/{pollId}")
    public ResponseEntity<?> editPoll(@PathVariable Long pollId, @RequestBody Poll updatedPoll, Authentication authentication) {
        // Get the user currently logged in
        String email = authentication.getName();
        Poll savedPoll = pollService.editPoll(pollId, updatedPoll, email);
        return ResponseEntity.ok(savedPoll);
    }

    // Delete a poll (Only the creator can)
    @DeleteMapping("/delete/{pollId}")
    public ResponseEntity<?> deletePoll(@PathVariable Long pollId, Authentication authentication) {
        // Get the user currently logged in
        String email = authentication.getName();
        pollService.deletePoll(pollId, email);
        return ResponseEntity.ok("Poll deleted successfully");
    }
}
