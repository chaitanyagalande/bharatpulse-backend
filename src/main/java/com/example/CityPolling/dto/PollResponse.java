package com.example.CityPolling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Used in PollWithVoteResponse to make a well crafted JSON to return while fetching polls
public class PollResponse {
    private Long id;
    private String question;
    private String optionOne;
    private String optionTwo;
    private String optionThree;
    private String optionFour;
    private String city;
    private CreatedByResponse createdBy;
    private LocalDateTime createdAt;
    private Long optionOneVotes;
    private Long optionTwoVotes;
    private Long optionThreeVotes;
    private Long optionFourVotes;
    private List<String> tags;
    private long commentCount;
}
