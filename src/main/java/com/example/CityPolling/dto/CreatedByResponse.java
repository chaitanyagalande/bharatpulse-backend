package com.example.CityPolling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Used in PollResponse to make a well crafted JSON to return while fetching polls
public class CreatedByResponse {
    private Long id;
    private String username;
}
