package com.example.CityPolling.controller;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.service.PollService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping("/city/{city}")
    public List<Poll> getPollsByCity(@PathVariable String city) {
        return pollService.getPollsByCity(city);
    }

    @PostMapping
    public Poll createPoll(@RequestBody Poll poll) {
        return pollService.createPoll(poll);
    }

    @GetMapping("/{id}")
    public Poll getPoll(@PathVariable Long id) {
        return pollService.getPollById(id);
    }
}
