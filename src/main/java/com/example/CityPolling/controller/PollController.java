package com.example.CityPolling.controller;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
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
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName(); // email stored as name in token
        Optional<User> userOpt = userService.findByEmail(email);

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
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        String city = userOpt.get().getCity();
        List<Poll> polls = pollService.getPollsByCity(city);
        return ResponseEntity.ok(polls);
    }

    // Edit a poll (Only the creator can)
    @PutMapping("/edit/{pollId}")
    public ResponseEntity<?> editPoll(@PathVariable Long pollId, @RequestBody Poll updatedPoll, Authentication authentication) {
        if(authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        // Get the user currently logged in
        String email = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        // Get the poll that needs to be edited
        Optional<Poll> pollOpt = pollService.findById(pollId);
        if(pollOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Poll not found");
        }

        // Check if that poll is created by the user (Ensure only creator can edit)
        Poll poll = pollOpt.get();
        if(!poll.getCreatedBy().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).body("You are not allowed to edit this poll.");
        }
        // Update fields
        poll.setQuestion(updatedPoll.getQuestion());
        poll.setOptionOne(updatedPoll.getOptionOne());
        poll.setOptionTwo(updatedPoll.getOptionTwo());
        poll.setOptionThree(updatedPoll.getOptionThree());
        poll.setOptionFour(updatedPoll.getOptionFour());

        Poll savedPoll = pollService.createPoll(poll);
        return ResponseEntity.ok(savedPoll);
    }

    // Delete a poll (Only the creator can)
    @DeleteMapping("/delete/{pollId}")
    public ResponseEntity<?> deletePoll(@PathVariable Long pollId, Authentication authentication) {
        if(authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        // Get the user currently logged in
        String email = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        // Get the poll that needs to be edited
        Optional<Poll> pollOpt = pollService.findById(pollId);
        if(pollOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Poll not found");
        }
        // Check if that poll is created by the user (Ensure only creator can delete)
        Poll poll = pollOpt.get();
        if(!poll.getCreatedBy().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).body("You are not allowed to delete this poll.");
        }

        pollService.deleteById(pollId);
        return ResponseEntity.ok("Poll deleted successfully");
    }


//    @GetMapping("/{id}")
//    public Poll getPoll(@PathVariable Long id) {
//        return pollService.getPollById(id);
//    }
}
