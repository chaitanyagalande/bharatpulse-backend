package com.example.CityPolling.service;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.dto.PasswordUpdateRequest;
import com.example.CityPolling.dto.UsernameUpdateRequest;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.model.Vote;
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

    public ProfileService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, PollRepository pollRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    public User getCurrentUser(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        user.setPassword(null); // hide password before returning
        return user;
    }

    public User updateCity(CityRequest request, String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        user.setCity(request.getCity());
        userRepository.save(user);
        user.setPassword(null); // hide password before returning
        return user;
    }

    public boolean updatePassword(PasswordUpdateRequest req, String email) {
        User user = userRepository.findByEmail(email);
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
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalStateException("Authenticated user not found in DB");
        }
        if (userRepository.existsByUsername(request.getNewUsername())) {
            return false; // username already taken
        }
        user.setUsername(request.getNewUsername());
        userRepository.save(user);
        return true;
    }

    public String toggleMode(String email) {
        User user = userRepository.findByEmail(email);
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
        User user = userRepository.findByEmail(email);

        // Delete all polls created by the user
        List<Poll> polls = pollRepository.findByCreatedBy(user.getId());
        for (Poll poll : polls) {
            // Delete all votes in this poll
            voteRepository.deleteByPollId(poll.getId());
            pollRepository.delete(poll);
        }

        // Delete all votes made by this user
        List<Vote> votes = voteRepository.findByUserId(user.getId());
        for (Vote vote : votes) {
            Poll poll = pollRepository.findById(vote.getPollId()).orElse(null);
            if (poll != null) {
                adjustVoteCounts(poll, vote.getSelectedOption(), -1);
                pollRepository.save(poll);
            }
        }
        voteRepository.deleteByUserId(user.getId());

        // Delete user itself
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
