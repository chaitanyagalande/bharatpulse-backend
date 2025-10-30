package com.example.CityPolling.service;

import com.example.CityPolling.model.Vote;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.repository.VoteRepository;
import com.example.CityPolling.repository.PollRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;

    public VoteService(VoteRepository voteRepository, PollRepository pollRepository) {
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
    }

    // ✅ Cast a vote
    public String castVote(Vote vote) {
        // Prevent double voting
        if (voteRepository.existsByPollIdAndUserId(vote.getPollId(), vote.getUserId())) {
            return "User has already voted on this poll.";
        }

        // Check poll exists
        Poll poll = pollRepository.findById(vote.getPollId()).orElse(null);
        if (poll == null) {
            return "Poll not found.";
        }

        // Validate selected option
        if (vote.getSelectedOption() < 1 || vote.getSelectedOption() > 4) {
            return "Invalid option number.";
        }

        voteRepository.save(vote);
        return "Vote recorded successfully!";
    }

    // 📊 Get vote results for a poll
    public Map<String, Long> getPollResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId).orElse(null);
        if (poll == null) return null;

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
