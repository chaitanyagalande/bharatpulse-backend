package com.example.CityPolling.repository;

import com.example.CityPolling.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByPollIdAndUserId(Long pollId, Long userId);

    List<Vote> findByPollId(Long pollId);

    // ✅ New: Group by selectedOption to count votes efficiently
    @Query("SELECT v.selectedOption, COUNT(v) FROM Vote v WHERE v.pollId = :pollId GROUP BY v.selectedOption")
    List<Object[]> countVotesByPollId(@Param("pollId") Long pollId);
}
