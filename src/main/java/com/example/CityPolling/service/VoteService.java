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

    public String castVote(Vote voteRequest, String email) {
        User user = userService.findByEmail(email);
        Poll poll = pollRepository.findById(voteRequest.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("Poll not found."));

        if (!poll.getCity().equalsIgnoreCase(user.getCity())) {
            throw new IllegalArgumentException("You can only vote on polls from your own city.");
        }

        int selectedOption = voteRequest.getSelectedOption();
        if (selectedOption < 1 || selectedOption > 4) {
            throw new IllegalArgumentException("Invalid option number.");
        }

        Optional<Vote> existingVoteOpt = voteRepository.findByPollIdAndUserId(poll.getId(), user.getId());

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();
            int oldOption = existingVote.getSelectedOption();

            // ✅ If same option clicked again, ignore
            if (oldOption == selectedOption) {
                return "You already voted for this option.";
            }

            // ✅ Adjust counts: decrement old, increment new
            adjustVoteCounts(poll, oldOption, -1);
            adjustVoteCounts(poll, selectedOption, +1);

            existingVote.setSelectedOption(selectedOption);
            existingVote.setVotedAt(LocalDateTime.now());
            voteRepository.save(existingVote);
            pollRepository.save(poll);

            return "Vote updated successfully!";
        }

        // ✅ First-time vote
        adjustVoteCounts(poll, selectedOption, +1);

        Vote newVote = new Vote();
        newVote.setPollId(poll.getId());
        newVote.setUserId(user.getId());
        newVote.setSelectedOption(selectedOption);
        voteRepository.save(newVote);
        pollRepository.save(poll);

        return "Vote recorded successfully!";
    }

    private void adjustVoteCounts(Poll poll, int option, int delta) {
        switch (option) {
            case 1 -> poll.setOptionOneVotes(poll.getOptionOneVotes() + delta);
            case 2 -> poll.setOptionTwoVotes(poll.getOptionTwoVotes() + delta);
            case 3 -> poll.setOptionThreeVotes(poll.getOptionThreeVotes() + delta);
            case 4 -> poll.setOptionFourVotes(poll.getOptionFourVotes() + delta);
        }
    }

    public Map<String, Long> getPollResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        Map<String, Long> results = new HashMap<>();
        results.put(poll.getOptionOne(), poll.getOptionOneVotes());
        results.put(poll.getOptionTwo(), poll.getOptionTwoVotes());
        if (poll.getOptionThree() != null) results.put(poll.getOptionThree(), poll.getOptionThreeVotes());
        if (poll.getOptionFour() != null) results.put(poll.getOptionFour(), poll.getOptionFourVotes());
        return results;
    }
}
