package com.example.CityPolling.controller;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.service.PollService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollService pollService;
    private final UserRepository userRepository;

    public PollController(PollService pollService, UserRepository userRepository) {
        this.pollService = pollService;
        this.userRepository = userRepository;
    }

    // Create a new poll (JWT protected)
    @PostMapping("/create")
    public ResponseEntity<?> createPoll(@RequestBody Poll poll, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName(); // email stored as name in token
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        poll.setCreatedBy(user.getId());
        poll.setCity(user.getCity()); // use the same city as user automatically

        Poll savedPoll = pollService.createPoll(poll);
        return ResponseEntity.ok(savedPoll);
    }

    // Get all polls for the logged-in user's city
    @GetMapping("/mycity")
    public ResponseEntity<?> getPollsForCity(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        String city = userOpt.get().getCity();
        List<Poll> polls = pollService.getPollsByCity(city);
        return ResponseEntity.ok(polls);
    }

//    @GetMapping("/{id}")
//    public Poll getPoll(@PathVariable Long id) {
//        return pollService.getPollById(id);
//    }
}
