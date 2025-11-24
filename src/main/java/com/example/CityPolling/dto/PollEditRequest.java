package com.example.CityPolling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollEditRequest {

    @NotBlank(message = "Question is required")
    @Size(min = 5, max = 255, message = "Question must be between 5 and 255 characters")
    private String question;

    @NotBlank(message = "Option 1 is required")
    @Size(max = 100, message = "Option 1 must not exceed 100 characters")
    private String optionOne;

    @NotBlank(message = "Option 2 is required")
    @Size(max = 100, message = "Option 2 must not exceed 100 characters")
    private String optionTwo;

    @Size(max = 100, message = "Option 3 must not exceed 100 characters")
    private String optionThree;

    @Size(max = 100, message = "Option 4 must not exceed 100 characters")
    private String optionFour;
}