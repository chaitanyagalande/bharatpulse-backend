package com.example.CityPolling.service;

import com.example.CityPolling.model.Poll;
import com.example.CityPolling.repository.PollRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PollService {
    private final PollRepository pollRepository;

    public PollService(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    public List<Poll> getPollsByCity(String city) {
        return pollRepository.findByCityIgnoreCase(city);
    }

    public Poll createPoll(Poll poll) {
        return pollRepository.save(poll);
    }

    public Optional<Poll> findById(Long pollId) {
        return pollRepository.findById(pollId);
    }

    public void deleteById(Long pollId) {
        pollRepository.deleteById(pollId);
    }

//    public Poll getPollById(Long id) {
//        return pollRepository.findById(id).orElse(null);
//    }
}
