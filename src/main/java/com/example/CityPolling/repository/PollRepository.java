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

//    @Query("""
//    SELECT p FROM Poll p
//    WHERE p.city = :city
//      AND EXISTS (
//        SELECT 1 FROM PollTag pt
//        JOIN pt.tag t
//        WHERE pt.poll.id = p.id
//          AND t.name IN :tags
//        GROUP BY pt.poll.id
//        HAVING COUNT(DISTINCT t.name) = :tagCount
//      )
//""") USE IF WANT TO INCREASE PERFORMANCE FOR RETURNING LARGE NUMBER OF POLLS
    @Query("""
    SELECT p FROM Poll p
    WHERE p.city = :city
    AND p.id IN (
        SELECT pt.poll.id
        FROM PollTag pt
        JOIN pt.tag t
        WHERE t.name IN :tags
        GROUP BY pt.poll.id
        HAVING COUNT(DISTINCT t.name) = :tagCount
    )
""")
    List<Poll> findPollsByCityAndTags(@Param("city") String city,
                                      @Param("tags") List<String> tags,
                                      @Param("tagCount") long tagCount);



}
