package com.example.CityPolling.dto;

import com.example.CityPolling.model.Poll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollCreateRequest {
    private Poll poll;
    // tag names, e.g. ["sports", "local", "food"]
    private List<String> tags;
}
