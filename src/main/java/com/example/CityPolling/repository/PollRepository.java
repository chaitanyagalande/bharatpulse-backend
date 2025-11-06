package com.example.CityPolling.repository;

import com.example.CityPolling.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByCityIgnoreCase(String city);

    List<Poll> findByCreatedBy(Long createdBy);

    Long countByCreatedBy(Long userId);

    @Query("""
    SELECT p FROM Poll p
    WHERE LOWER(p.city) = LOWER(:city)
      AND (
        LOWER(p.question) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.optionOne) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.optionTwo) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.optionThree) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.optionFour) LIKE LOWER(CONCAT('%', :query, '%'))
      )
    """)
    List<Poll> searchPollsByCityAndKeyword(@Param("city") String city, @Param("query") String query);

}
