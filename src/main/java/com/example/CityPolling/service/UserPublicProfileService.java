package com.example.CityPolling.service;

import com.example.CityPolling.dto.*;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.model.Vote;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPublicProfileService {
    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserService userService;

    public UserPublicProfileService(UserRepository userRepository, PollRepository pollRepository, VoteRepository voteRepository, UserService userService) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userService = userService;
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

    // User Public Profile info
    public UserPublicProfileResponse getUserPublicProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Poll> createdPolls = pollRepository.findByCreatedBy(userId);
        List<Vote> votes = voteRepository.findByUserId(userId);

        Long totalCreatedCount = (long) createdPolls.size();
        Long totalVotedCount = (long) votes.size();
        long totalActivity = totalCreatedCount + totalVotedCount;

        Map<String, Long> createdCountMap = new HashMap<>();
        Map<String, Long> votedCountMap = new HashMap<>();

        // ✅ Aggregate created polls per city
        for (Poll poll : createdPolls) {
            if (poll.getCity() != null) {
                createdCountMap.merge(poll.getCity(), 1L, Long::sum);
            }
        }

        // ✅ Aggregate voted polls per city
        if (!votes.isEmpty()) {
            List<Long> votedPollIds = votes.stream()
                    .map(Vote::getPollId)
                    .toList();

            List<Poll> votedPolls = pollRepository.findAllById(votedPollIds);
            for (Poll poll : votedPolls) {
                if (poll.getCity() != null) {
                    votedCountMap.merge(poll.getCity(), 1L, Long::sum);
                }
            }
        }

        // ✅ Merge both maps and calculate percentage per city
        Set<String> allCities = new HashSet<>();
        allCities.addAll(createdCountMap.keySet());
        allCities.addAll(votedCountMap.keySet());

        List<CityActivityResponse> cityActivityList = allCities.stream()
                .map(city -> {
                    long created = createdCountMap.getOrDefault(city, 0L);
                    long voted = votedCountMap.getOrDefault(city, 0L);
                    long cityTotal = created + voted;

                    double percentage = totalActivity > 0
                            ? (cityTotal * 100.0 / totalActivity)
                            : 0.0;

                    return new CityActivityResponse(city, created, voted, percentage);
                })
                // Sort by total activity descending
                .sorted((a, b) -> Long.compare(
                        (b.getPollsCreatedCount() + b.getPollsVotedCount()),
                        (a.getPollsCreatedCount() + a.getPollsVotedCount())
                ))
                .toList();

        return new UserPublicProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getCity(),
                totalCreatedCount,
                totalVotedCount,
                cityActivityList
        );
    }

    // Polls created by user
    public List<PollWithVoteResponse> getPollsCreatedByUser(Long userId, String sortBy) {
        List<Poll> polls = pollRepository.findByCreatedBy(userId);

        List<PollWithVoteResponse> responses = polls.stream()
                .map(poll -> new PollWithVoteResponse(
                        buildPollResponse(poll),
                        null,
                        null
                ))
                .toList();

        return sortPolls(responses, sortBy);
    }

    // Polls voted by user (This will show that user's voted option and time not own, handle this presentation login in frontend)
    public List<PollWithVoteResponse> getPollsVotedByUser(Long userId, String sortBy) {
        List<Vote> votes = voteRepository.findByUserId(userId);
        List<Long> pollIds = votes.stream().map(Vote::getPollId).distinct().toList();

        Map<Long, Poll> pollMap = pollRepository.findAllById(pollIds).stream()
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
