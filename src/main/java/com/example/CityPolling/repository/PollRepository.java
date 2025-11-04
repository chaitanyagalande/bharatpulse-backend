package com.example.CityPolling.repository;

import com.example.CityPolling.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByCityIgnoreCase(String city);

    List<Poll> findByCreatedBy(Long createdBy);

    Long countByCreatedBy(Long userId);
}
