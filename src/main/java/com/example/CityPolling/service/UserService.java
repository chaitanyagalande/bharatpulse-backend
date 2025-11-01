package com.example.CityPolling.service;

import com.example.CityPolling.dto.RegisterRequest;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest req) {
        if(userRepository.existsByEmail(req.getEmail())) {
            return null; // signal to controller that email is already used
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        String hashed = passwordEncoder.encode(req.getPassword());
        user.setPassword(hashed);
        user.setCity(req.getCity());
        user.setRole("USER");
        return userRepository.save(user);
    }

    // Needed when login needs to be done
    public boolean validateCredentials(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty()) return false;
        String storedHash = userOpt.get().getPassword();
        return passwordEncoder.matches(rawPassword, storedHash);
    }

//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
