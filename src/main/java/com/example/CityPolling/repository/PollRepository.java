package com.example.CityPolling.repository;

import com.example.CityPolling.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByCityIgnoreCase(String city);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.pollId = :pollId")
    long countVotesForPoll(@Param("pollId") Long id);

    List<Poll> findByCreatedBy(Long createdBy);
}
