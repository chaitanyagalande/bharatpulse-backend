package com.example.CityPolling.repository;

import com.example.CityPolling.model.PollTag;
import com.example.CityPolling.model.PollTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PollTagRepository extends JpaRepository<PollTag, PollTagId> {
    List<PollTag> findById_PollId(Long pollId);

    void deleteById_PollId(Long pollId);
}
