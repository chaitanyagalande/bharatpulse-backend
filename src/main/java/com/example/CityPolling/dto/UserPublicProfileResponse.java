package com.example.CityPolling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicProfileResponse {
    private Long id;
    private String username;
    private String city;
    private Long totalPollsCreatedCount;
    private Long totalPollsVotedCount;
    private List<CityActivityResponse> activeCities;
}
