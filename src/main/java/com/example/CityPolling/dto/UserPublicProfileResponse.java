package com.example.CityPolling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicProfileResponse {
    private Long id;
    private String username;
    private String city;
    private Long pollsCreatedCount;
    private Long pollsVotedCount;
}
