package com.example.CityPolling.dto;

import com.example.CityPolling.model.Poll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollWithVoteResponse {
    private Poll poll;              // poll details (with vote counts)
    private Integer selectedOption;  // null if user hasn't voted
    private LocalDateTime votedAt;  // null if user hasn't voted
}