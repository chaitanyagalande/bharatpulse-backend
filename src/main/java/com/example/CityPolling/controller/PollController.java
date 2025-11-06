package com.example.CityPolling.controller;

import com.example.CityPolling.dto.PollWithVoteResponse;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.service.PollService;
import com.example.CityPolling.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    // Create a new poll (JWT protected)
    @PostMapping("/create")
    public ResponseEntity<?> createPoll(@RequestBody Poll poll, Authentication authentication) {
        String email = authentication.getName(); // email stored as name in token
        Poll savedPoll = pollService.createPoll(poll, email);
        return ResponseEntity.ok(savedPoll);
    }

    // Get all polls for the logged-in user's city, with sorting
    // GET /api/polls/feed?sortBy=latest
    // GET /api/polls/feed?sortBy=oldest
    // GET /api/polls/feed?sortBy=mostVoted
    @GetMapping("/feed")
    public ResponseEntity<?> getPollFeed(Authentication authentication, @RequestParam(defaultValue = "latest") String sortBy) {
        String email = authentication.getName();
        List<PollWithVoteResponse> polls = pollService.getPollFeed(email, sortBy);
        return ResponseEntity.ok(polls);
    }

    // Show all polls created by logged-in user.
    @GetMapping("/my-polls")
    public ResponseEntity<?> getMyPolls(Authentication authentication, @RequestParam(defaultValue = "latest") String sortBy) {
        String email = authentication.getName();
        List<PollWithVoteResponse> myPolls = pollService.getMyPolls(email, sortBy);
        return ResponseEntity.ok(myPolls);
    }

    @GetMapping("/my-votes")
    public ResponseEntity<?> getMyVotedPolls(Authentication authentication, @RequestParam(defaultValue = "latestvoted") String sortBy) {
        String email = authentication.getName();
        List<PollWithVoteResponse> votedPolls = pollService.getMyVotedPolls(email, sortBy);
        return ResponseEntity.ok(votedPolls);
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

    // localhost:8080/api/polls/search?query=mumbai&sortBy=oldest (This is how url would be)
    @GetMapping("/search")
    public ResponseEntity<?> searchPolls(
            @RequestParam String query,
            @RequestParam(defaultValue = "latest") String sortBy,
            Authentication authentication) {

        String email = authentication.getName();
        List<PollWithVoteResponse> results = pollService.searchPolls(query, email, sortBy);
        return ResponseEntity.ok(results);
    }

}
