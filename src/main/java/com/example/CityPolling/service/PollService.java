package com.example.CityPolling.service;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.PollRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PollService {
    private final PollRepository pollRepository;
    private final UserService userService;

    public PollService(PollRepository pollRepository, UserService userService) {
        this.pollRepository = pollRepository;
        this.userService = userService;
    }

    // ✅ Create poll — unchanged
    public Poll createPoll(Poll poll, String email) {
        User user = userService.findByEmail(email);
        poll.setCreatedBy(user.getId());
        poll.setCity(user.getCity()); // use same city as user
        return pollRepository.save(poll);
    }

    // Get feed of polls in own city
    // If explore mode, just return same polls — frontend can show results with them
    // If local mode, frontend hides results (backend behavior same)
    public List<Poll> getPollFeed(String email, String sortBy) {
        User user = userService.findByEmail(email);
        String city = user.getCity();

        // Always fetch user's city polls
        List<Poll> polls = pollRepository.findByCityIgnoreCase(city);

        // Sorting logic
        return switch (sortBy.toLowerCase()) {
            case "oldest" -> polls.stream()
                    .sorted(Comparator.comparing(Poll::getCreatedAt))
                    .toList();

            case "mostvoted" -> polls.stream()
                    .sorted((p1, p2) -> Long.compare(
                            getTotalVotes(p2),
                            getTotalVotes(p1)
                    ))
                    .toList();

            default -> polls.stream()
                    .sorted(Comparator.comparing(Poll::getCreatedAt).reversed())
                    .toList();
        };
    }

    // ✅ Utility to count votes
    private long getTotalVotes(Poll poll) {
        long total = 0;
        if (poll.getOptionOneVotes() != null) total += poll.getOptionOneVotes();
        if (poll.getOptionTwoVotes() != null) total += poll.getOptionTwoVotes();
        if (poll.getOptionThreeVotes() != null) total += poll.getOptionThreeVotes();
        if (poll.getOptionFourVotes() != null) total += poll.getOptionFourVotes();
        return total;
    }

    // ✅ Edit poll — unchanged
    public Poll editPoll(Long pollId, Poll updatedPoll, String email) {
        User user = userService.findByEmail(email);
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (!poll.getCreatedBy().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to edit this poll.");
        }

        poll.setQuestion(updatedPoll.getQuestion());
        poll.setOptionOne(updatedPoll.getOptionOne());
        poll.setOptionTwo(updatedPoll.getOptionTwo());
        poll.setOptionThree(updatedPoll.getOptionThree());
        poll.setOptionFour(updatedPoll.getOptionFour());

        return pollRepository.save(poll);
    }

    // ✅ Delete poll — unchanged
    public void deletePoll(Long pollId, String email) {
        User user = userService.findByEmail(email);
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (!poll.getCreatedBy().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to delete this poll.");
        }

        pollRepository.deleteById(pollId);
    }
}
