package com.example.CityPolling.service;

import com.example.CityPolling.dto.CreatedByResponse;
import com.example.CityPolling.dto.PollResponse;
import com.example.CityPolling.dto.PollWithVoteResponse;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.model.Vote;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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

    // ✅ Create poll
    public Poll createPoll(Poll poll, String email) {
        User user = userService.findByEmail(email);
        // ✅ Must have at least 2 options
        if (poll.getOptionOne() == null || poll.getOptionOne().isBlank() ||
                poll.getOptionTwo() == null || poll.getOptionTwo().isBlank()) {
            throw new IllegalArgumentException("Poll must have at least two options.");
        }

        // ✅ Normalize optional options (empty → null)
        poll.setOptionThree(
                (poll.getOptionThree() != null && !poll.getOptionThree().isBlank())
                        ? poll.getOptionThree() : null
        );
        poll.setOptionFour(
                (poll.getOptionFour() != null && !poll.getOptionFour().isBlank())
                        ? poll.getOptionFour() : null
        );

        poll.setCreatedBy(user.getId());
        poll.setCity(user.getCity());
        return pollRepository.save(poll);
    }

    // ✅ Build PollResponse (core helper)
    private PollResponse buildPollResponse(Poll poll) {
        User creator = userService.findById(poll.getCreatedBy());
        CreatedByResponse createdBy = new CreatedByResponse(creator.getId(), creator.getUsername());

        return new PollResponse(
                poll.getId(),
                poll.getQuestion(),
                poll.getOptionOne(),
                poll.getOptionTwo(),
                poll.getOptionThree(),
                poll.getOptionFour(),
                poll.getCity(),
                createdBy,
                poll.getCreatedAt(),
                poll.getOptionOneVotes(),
                poll.getOptionTwoVotes(),
                poll.getOptionThreeVotes(),
                poll.getOptionFourVotes()
        );
    }

    // ✅ Feed of polls in user's city
    public List<PollWithVoteResponse> getPollFeed(String email, String sortBy) {
        User user = userService.findByEmail(email);
        String city = user.getCity();

        List<Poll> polls = pollRepository.findByCityIgnoreCase(city);
        List<Vote> userVotes = voteRepository.findByUserId(user.getId());

        Map<Long, Vote> voteMap = userVotes.stream()
                .collect(Collectors.toMap(Vote::getPollId, v -> v));

        List<PollWithVoteResponse> responses = polls.stream()
                .map(p -> {
                    Vote v = voteMap.get(p.getId());
                    return new PollWithVoteResponse(
                            buildPollResponse(p),
                            v != null ? v.getSelectedOption() : null,
                            v != null ? v.getVotedAt() : null
                    );
                })
                .toList();

        return sortPolls(responses, sortBy);
    }

    // ✅ Polls created by me
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
                            buildPollResponse(poll),
                            v != null ? v.getSelectedOption() : null,
                            v != null ? v.getVotedAt() : null
                    );
                })
                .toList();

        return sortPolls(responses, sortBy);
    }

    // ✅ Polls I have voted on
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
                            buildPollResponse(poll),
                            vote.getSelectedOption(),
                            vote.getVotedAt()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return sortPolls(responses, sortBy);
    }

    // ✅ Edit poll (Can only edit poll if poll is less than 5 mins old and no one has yet voted)
    public Poll editPoll(Long pollId, Poll updatedPoll, String email) {
        User user = userService.findByEmail(email);

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        // Ensure only the creator can edit
        if (!poll.getCreatedBy().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to edit this poll.");
        }

        // Ensure the poll is less than 5 minutes old
        LocalDateTime now = LocalDateTime.now();
        if (poll.getCreatedAt().isBefore(now.minusMinutes(5))) {
            throw new IllegalArgumentException("Poll can only be edited within 5 minutes of creation.");
        }

        // Ensure no votes have been cast
        long totalVotes = Optional.ofNullable(poll.getOptionOneVotes()).orElse(0L)
                + Optional.ofNullable(poll.getOptionTwoVotes()).orElse(0L)
                + Optional.ofNullable(poll.getOptionThreeVotes()).orElse(0L)
                + Optional.ofNullable(poll.getOptionFourVotes()).orElse(0L);

        if (totalVotes > 0) {
            throw new IllegalArgumentException("Poll cannot be edited after votes have been cast.");
        }

        // ✅ Validation: must have at least 2 options
        if (updatedPoll.getOptionOne() == null || updatedPoll.getOptionOne().isBlank() ||
                updatedPoll.getOptionTwo() == null || updatedPoll.getOptionTwo().isBlank()) {
            throw new IllegalArgumentException("Poll must have at least two options.");
        }

        // ✅ Update + normalize in one go
        poll.setQuestion(updatedPoll.getQuestion().trim());
        poll.setOptionOne(updatedPoll.getOptionOne().trim());
        poll.setOptionTwo(updatedPoll.getOptionTwo().trim());

        // Normalize optional options (blank → null)
        poll.setOptionThree(
                (updatedPoll.getOptionThree() != null && !updatedPoll.getOptionThree().isBlank())
                        ? updatedPoll.getOptionThree().trim() : null
        );
        poll.setOptionFour(
                (updatedPoll.getOptionFour() != null && !updatedPoll.getOptionFour().isBlank())
                        ? updatedPoll.getOptionFour().trim() : null
        );

        // Save and return — same pollId retained
        return pollRepository.save(poll);
    }


    // ✅ Delete poll (Delete all votes of poll first then poll)
    @Transactional
    public void deletePoll(Long pollId, String email) {
        User user = userService.findByEmail(email);
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (!poll.getCreatedBy().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to delete this poll.");
        }

        // Delete all votes related to this poll
        voteRepository.deleteByPollId(pollId);
        // Delete the poll itself
        pollRepository.deleteById(pollId);
    }

    // 🔍 Search polls by keyword within user's city
    public List<PollWithVoteResponse> searchPolls(String query, String email, String sortBy) {
        User user = userService.findByEmail(email);
        String city = user.getCity();

        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty.");
        }

        // ✅ Fetch polls filtered by city and keyword
        List<Poll> polls = pollRepository.searchPollsByCityAndKeyword(city, query.trim());

        // ✅ Get user's votes for marking voted options
        List<Vote> userVotes = voteRepository.findByUserId(user.getId());
        Map<Long, Vote> voteMap = userVotes.stream()
                .collect(Collectors.toMap(Vote::getPollId, v -> v));

        // ✅ Convert to PollWithVoteResponse
        List<PollWithVoteResponse> responses = polls.stream()
                .map(p -> {
                    Vote v = voteMap.get(p.getId());
                    return new PollWithVoteResponse(
                            buildPollResponse(p),
                            v != null ? v.getSelectedOption() : null,
                            v != null ? v.getVotedAt() : null
                    );
                })
                .toList();

        // ✅ Sort and return
        return sortPolls(responses, sortBy);
    }

    // 🧮 Utility: total votes
    private long getTotalVotes(Poll poll) {
        return Optional.ofNullable(poll.getOptionOneVotes()).orElse(0L)
                + Optional.ofNullable(poll.getOptionTwoVotes()).orElse(0L)
                + Optional.ofNullable(poll.getOptionThreeVotes()).orElse(0L)
                + Optional.ofNullable(poll.getOptionFourVotes()).orElse(0L);
    }

    // 📊 Sorting helper
    private List<PollWithVoteResponse> sortPolls(List<PollWithVoteResponse> responses, String sortBy) {
        Comparator<PollWithVoteResponse> comparator;

        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "oldest" ->
                    comparator = Comparator.comparing(r -> r.getPoll().getCreatedAt());
            case "mostvoted" ->
                    comparator = Comparator.comparing((PollWithVoteResponse r) -> getTotalVotes(toPoll(r))).reversed();
            case "latestvoted" ->
                    comparator = Comparator.comparing(PollWithVoteResponse::getVotedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            default ->
                    comparator = Comparator.comparing((PollWithVoteResponse r) -> r.getPoll().getCreatedAt()).reversed();
        }

        return responses.stream()
                .sorted(comparator)
                .toList();
    }

    private Poll toPoll(PollWithVoteResponse r) {
        PollResponse pr = r.getPoll();
        Poll p = new Poll();
        p.setOptionOneVotes(pr.getOptionOneVotes());
        p.setOptionTwoVotes(pr.getOptionTwoVotes());
        p.setOptionThreeVotes(pr.getOptionThreeVotes());
        p.setOptionFourVotes(pr.getOptionFourVotes());
        return p;
    }
}
