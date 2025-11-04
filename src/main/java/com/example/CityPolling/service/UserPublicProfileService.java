package com.example.CityPolling.service;

import com.example.CityPolling.dto.UserPublicProfileResponse;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.repository.VoteRepository;
import org.springframework.stereotype.Service;

@Service
public class UserPublicProfileService {
    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public UserPublicProfileService(UserRepository userRepository, PollRepository pollRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    public UserPublicProfileResponse getUserPublicProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Long createdCount = pollRepository.countByCreatedBy(userId);
        Long votedCount = voteRepository.countByUserId(userId);
        return new UserPublicProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getCity(),
                createdCount,
                votedCount
        );
    }
}
