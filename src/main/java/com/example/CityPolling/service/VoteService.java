package com.example.CityPolling.service;

import com.example.CityPolling.model.User;
import com.example.CityPolling.model.Vote;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.repository.VoteRepository;
import com.example.CityPolling.repository.PollRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final UserService userService;

    public VoteService(VoteRepository voteRepository, PollRepository pollRepository, UserService userService) {
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.userService = userService;
    }

    // Cast a vote
    public String castVote(Vote voteRequest, String email) {
        User user = userService.findByEmail(email); // Authenticated user
        Poll poll = pollRepository.findById(voteRequest.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("Poll not found."));

        // Prevent users from voting on polls outside their city even though users only see polls from their city (Just for safety)
        if (!poll.getCity().equalsIgnoreCase(user.getCity())) {
            throw new IllegalArgumentException("You can only vote on polls from your own city.");
        }

        // Validate selected option
        if (voteRequest.getSelectedOption() < 1 || voteRequest.getSelectedOption() > 4) {
            throw new IllegalArgumentException("Invalid option number.");
        }

        // Check if user has already voted
        Optional<Vote> existingVoteOpt = voteRepository.findByPollIdAndUserId(poll.getId(), user.getId());

        if (existingVoteOpt.isPresent()) {
            // User already voted — update existing vote
            Vote existingVote = existingVoteOpt.get();

            // If user clicks same option again — ignore
            if (existingVote.getSelectedOption().equals(voteRequest.getSelectedOption())) {
                return "You already voted for this option.";
            }
            existingVote.setSelectedOption(voteRequest.getSelectedOption());
            existingVote.setVotedAt(LocalDateTime.now());
            voteRepository.save(existingVote);
            return "Vote updated successfully!";
        }

        // User has not voted before — create new vote
        Vote newVote = new Vote();
        newVote.setPollId(poll.getId());
        newVote.setUserId(user.getId());
        newVote.setSelectedOption(voteRequest.getSelectedOption());
        voteRepository.save(newVote);

        return "Vote recorded successfully!";
    }

    // Get vote results for a poll
    public Map<String, Long> getPollResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        Map<String, Long> results = new HashMap<>();
        results.put(poll.getOptionOne(), 0L);
        results.put(poll.getOptionTwo(), 0L);
        if (poll.getOptionThree() != null) results.put(poll.getOptionThree(), 0L);
        if (poll.getOptionFour() != null) results.put(poll.getOptionFour(), 0L);

        List<Object[]> counts = voteRepository.countVotesByPollId(pollId);
        for (Object[] row : counts) {
            Integer option = (Integer) row[0];
            Long count = (Long) row[1];
            switch (option) {
                case 1 -> results.put(poll.getOptionOne(), count);
                case 2 -> results.put(poll.getOptionTwo(), count);
                case 3 -> results.put(poll.getOptionThree(), count);
                case 4 -> results.put(poll.getOptionFour(), count);
            }
        }

        return results;
    }
}
