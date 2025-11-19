package com.example.CityPolling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollEditRequest {
    private String question;
    private String optionOne;
    private String optionTwo;
    private String optionThree;
    private String optionFour;
}

