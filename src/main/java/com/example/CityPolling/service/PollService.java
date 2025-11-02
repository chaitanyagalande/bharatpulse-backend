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

    public Poll createPoll(Poll poll, String email) {
        User user = userService.findByEmail(email);
        poll.setCreatedBy(user.getId());
        poll.setCity(user.getCity()); // use the same city as user automatically
        return pollRepository.save(poll);
    }

    public List<Poll> getPollFeed(String email, String sortBy) {
        User user = userService.findByEmail(email);
        String city = user.getCity();
        List<Poll> polls = pollRepository.findByCityIgnoreCase(city);
        return switch(sortBy.toLowerCase()) {
            case "oldest" -> polls.stream()
                    .sorted(Comparator.comparing(Poll::getCreatedAt))
                    .toList();

            case "mostvoted" -> polls.stream()
                    .sorted((p1, p2) -> Long.compare(
                            pollRepository.countVotesForPoll(p2.getId()),
                            pollRepository.countVotesForPoll(p1.getId())
                    ))
                    .toList();

            default -> polls.stream() // "latest"
                    .sorted(Comparator.comparing(Poll::getCreatedAt).reversed())
                    .toList();
        };
    }

    public Poll editPoll(Long pollId, Poll updatedPoll, String email) {
        User user = userService.findByEmail(email); // authenticated user, guaranteed to exist
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (!poll.getCreatedBy().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to edit this poll.");
        }

        // Update allowed fields
        poll.setQuestion(updatedPoll.getQuestion());
        poll.setOptionOne(updatedPoll.getOptionOne());
        poll.setOptionTwo(updatedPoll.getOptionTwo());
        poll.setOptionThree(updatedPoll.getOptionThree());
        poll.setOptionFour(updatedPoll.getOptionFour());

        return pollRepository.save(poll);
    }

    public void deletePoll(Long pollId, String email) {
        User user = userService.findByEmail(email); // Authenticated user, guaranteed to exist
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (!poll.getCreatedBy().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to delete this poll.");
        }

        pollRepository.deleteById(pollId);
    }
}
