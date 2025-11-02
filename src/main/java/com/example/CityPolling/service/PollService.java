package com.example.CityPolling.service;

import com.example.CityPolling.dto.PollWithVoteResponse;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.model.Vote;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PollService {
    private final PollRepository pollRepository;
    private final UserService userService;
    private final VoteRepository voteRepository;

    public PollService(PollRepository pollRepository, UserService userService, VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.userService = userService;
        this.voteRepository = voteRepository;
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
    public List<PollWithVoteResponse> getPollFeed(String email, String sortBy) {
        User user = userService.findByEmail(email);
        String city = user.getCity();

        List<Poll> polls = pollRepository.findByCityIgnoreCase(city);
        List<Vote> userVotes = voteRepository.findByUserId(user.getId());
        Map<Long, Vote> voteMap = userVotes.stream()
                .collect(Collectors.toMap(Vote::getPollId, v -> v));

        List<PollWithVoteResponse> responses = polls.stream()
                .map(poll -> {
                    Vote v = voteMap.get(poll.getId());
                    return new PollWithVoteResponse(
                            poll,
                            v != null ? v.getSelectedOption() : null,
                            v != null ? v.getVotedAt() : null
                    );
                })
                .toList();

        return sortPolls(responses, sortBy);
    }

    public List<PollWithVoteResponse> getMyPolls(String email, String sortBy) {
        User user = userService.findByEmail(email);
        Long userId = user.getId();

        List<Poll> myPolls = pollRepository.findByCreatedBy(userId);
        List<Vote> userVotes = voteRepository.findByUserId(userId);

        Map<Long, Vote> voteMap = userVotes.stream()
                .collect(Collectors.toMap(Vote::getPollId, v -> v));

        List<PollWithVoteResponse> responses = myPolls.stream()
                .map(poll -> {
                    Vote v = voteMap.get(poll.getId());
                    return new PollWithVoteResponse(
                            poll,
                            v != null ? v.getSelectedOption() : null,
                            v != null ? v.getVotedAt() : null
                    );
                })
                .toList();

        return sortPolls(responses, sortBy);
    }


    public List<PollWithVoteResponse> getMyVotedPolls(String email, String sortBy) {
        User user = userService.findByEmail(email);
        Long userId = user.getId();

        List<Vote> votes = voteRepository.findByUserId(userId);
        List<Long> pollIds = votes.stream().map(Vote::getPollId).distinct().toList();
        List<Poll> polls = pollRepository.findAllById(pollIds);

        Map<Long, Poll> pollMap = polls.stream()
                .collect(Collectors.toMap(Poll::getId, p -> p));

        List<PollWithVoteResponse> responses = votes.stream()
                .map(vote -> {
                    Poll poll = pollMap.get(vote.getPollId());
                    if (poll == null) return null;
                    return new PollWithVoteResponse(
                            poll,
                            vote.getSelectedOption(),
                            vote.getVotedAt()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return sortPolls(responses, sortBy);
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

    // Utility to count votes
    private long getTotalVotes(Poll poll) {
        long total = 0;
        if (poll.getOptionOneVotes() != null) total += poll.getOptionOneVotes();
        if (poll.getOptionTwoVotes() != null) total += poll.getOptionTwoVotes();
        if (poll.getOptionThreeVotes() != null) total += poll.getOptionThreeVotes();
        if (poll.getOptionFourVotes() != null) total += poll.getOptionFourVotes();
        return total;
    }

    // Utility to sort votes
    private List<PollWithVoteResponse> sortPolls(List<PollWithVoteResponse> responses, String sortBy) {
        Comparator<PollWithVoteResponse> comparator;

        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "oldest" ->
                    comparator = Comparator.comparing(r -> r.getPoll().getCreatedAt());
            case "mostvoted" ->
                    comparator = Comparator.comparing((PollWithVoteResponse r) -> getTotalVotes(r.getPoll())).reversed();
            case "latestvoted" ->
                    comparator = Comparator.comparing(PollWithVoteResponse::getVotedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            default ->
                    comparator = Comparator.comparing((PollWithVoteResponse r) -> r.getPoll().getCreatedAt()).reversed();
        }

        return responses.stream()
                .sorted(comparator)
                .toList();
    }
}
