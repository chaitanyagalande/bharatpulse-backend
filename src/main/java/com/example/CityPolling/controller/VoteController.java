package com.example.CityPolling.controller;

import com.example.CityPolling.model.Vote;
import com.example.CityPolling.service.VoteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/cast")
    public String castVote(@RequestBody Vote vote) {
        return voteService.castVote(vote);
    }

    @GetMapping("/results/{pollId}")
    public Map<String, Long> getPollResults(@PathVariable Long pollId) {
        return voteService.getPollResults(pollId);
    }
}
