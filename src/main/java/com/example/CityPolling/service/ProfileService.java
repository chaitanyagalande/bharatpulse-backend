package com.example.CityPolling.service;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.dto.PasswordUpdateRequest;
import com.example.CityPolling.dto.UsernameUpdateRequest;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.model.Vote;
import com.example.CityPolling.repository.CommentRepository;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.UserRepository;
import com.example.CityPolling.repository.VoteRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final TagService tagService;
    private final CommentRepository commentRepository;

    public ProfileService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, PollRepository pollRepository, VoteRepository voteRepository, TagService tagService, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.tagService = tagService;
        this.commentRepository = commentRepository;
    }

    public User getCurrentUser(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim());
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        user.setPassword(null); // hide password before returning
        return user;
    }

    public User updateCity(CityRequest request, String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim());
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        // ✅ Convert city to lowercase
        user.setCity(request.getCity().toLowerCase().trim());
        userRepository.save(user);
        user.setPassword(null); // hide password before returning
        return user;
    }

    public boolean updatePassword(PasswordUpdateRequest req, String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim());
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            return false; // incorrect old password
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean updateUsername(UsernameUpdateRequest request, String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim());
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        // ✅ Convert to lowercase for uniqueness check
        String normalizedUsername = request.getNewUsername().toLowerCase().trim();
        if (userRepository.existsByUsername(normalizedUsername)) {
            return false; // username already taken
        }
        user.setUsername(normalizedUsername);
        userRepository.save(user);
        return true;
    }

    public String toggleMode(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim());
        String currentMode = user.getMode();
        if ("LOCAL".equalsIgnoreCase(currentMode)) {
            user.setMode("EXPLORE");
        } else {
            user.setMode("LOCAL");
        }
        userRepository.save(user); // ✅ persist change
        return user.getMode();
    }

    @Transactional
    public void deleteUserAccount(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim());
        Long userId = user.getId();
        // Delete all comments DONE BY THE USER
        commentRepository.deleteByUserId(userId);

        // Find all polls created by the user
        List<Poll> polls = pollRepository.findByCreatedBy(userId);
        for (Poll poll : polls) {
            Long pollId = poll.getId();

            // 1 Delete all comments ON THIS POLL
            commentRepository.deleteByPollId(pollId);

            // 2 Delete tags for this poll
            tagService.deleteTagsForPoll(pollId);

            // 3 Delete all votes inside this poll
            voteRepository.deleteByPollId(pollId);

            // 4 Delete the actual poll
            pollRepository.delete(poll);
        }

        // Delete all votes made BY the user
        List<Vote> votes = voteRepository.findByUserId(userId);
        for (Vote vote : votes) {
            Poll poll = pollRepository.findById(vote.getPollId()).orElse(null);
            if (poll != null) {
                adjustVoteCounts(poll, vote.getSelectedOption(), -1);
                pollRepository.save(poll);
            }
        }
        voteRepository.deleteByUserId(userId);

        // Finally delete the user
        userRepository.delete(user);
    }


    private void adjustVoteCounts(Poll poll, int option, int delta) {
        switch (option) {
            case 1 -> poll.setOptionOneVotes(poll.getOptionOneVotes() + delta);
            case 2 -> poll.setOptionTwoVotes(poll.getOptionTwoVotes() + delta);
            case 3 -> poll.setOptionThreeVotes(poll.getOptionThreeVotes() + delta);
            case 4 -> poll.setOptionFourVotes(poll.getOptionFourVotes() + delta);
        }
    }
}
