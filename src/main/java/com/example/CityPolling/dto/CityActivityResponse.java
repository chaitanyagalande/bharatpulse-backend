package com.example.CityPolling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// This will be used inside UserPublicProfileResponse to show stats
public class CityActivityResponse {
    private String city;
    private Long pollsCreatedCount;
    private Long pollsVotedCount;
    private double percentage;
}
