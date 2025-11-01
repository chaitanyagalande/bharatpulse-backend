package com.example.CityPolling.service;

import com.example.CityPolling.dto.CityRequest;
import com.example.CityPolling.dto.PasswordUpdateRequest;
import com.example.CityPolling.dto.UsernameUpdateRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
