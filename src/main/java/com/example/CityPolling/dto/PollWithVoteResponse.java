package com.example.CityPolling.dto;

import com.example.CityPolling.model.Poll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
// When a poll is fetched (like in feed, mypolls, myvotes, etc)to be shown on frontend this JSON will be sent
public class PollWithVoteResponse {
    private PollResponse poll;              // poll details (with vote counts)
    private Integer selectedOption;  // null if user hasn't voted
    private LocalDateTime votedAt;  // null if user hasn't voted
}