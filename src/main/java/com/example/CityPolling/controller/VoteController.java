package com.example.CityPolling.controller;

import com.example.CityPolling.model.Vote;
import com.example.CityPolling.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/cast")
    public ResponseEntity<?> castVote(@RequestBody Vote vote, Authentication authentication) {
        String email = authentication.getName();
        String message = voteService.castVote(vote, email);
        return ResponseEntity.ok(message);
    }
}
