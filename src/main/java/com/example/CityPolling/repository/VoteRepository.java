package com.example.CityPolling.repository;

import com.example.CityPolling.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPollIdAndUserId(Long id, Long id1);

    List<Vote> findByUserId(Long id);
}
