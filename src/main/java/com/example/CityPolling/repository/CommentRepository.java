package com.example.CityPolling.repository;

import com.example.CityPolling.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPollIdOrderByCreatedAtDesc(Long pollId);
    void deleteByPollId(Long pollId);
    void deleteByUserId(Long userId);
    long countByPollId(Long pollId);
}
