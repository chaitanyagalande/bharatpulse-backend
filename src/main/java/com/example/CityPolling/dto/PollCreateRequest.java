package com.example.CityPolling.dto;

import com.example.CityPolling.model.Poll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollCreateRequest {
    @Valid
    @NotNull(message = "Poll data is required")
    private Poll poll;
    // tag names, e.g. ["sports", "local", "food"]
    @Size(max = 5, message = "Maximum 5 tags allowed")
    private List<@NotBlank(message = "Tag cannot be blank")
    @Size(min = 1, max = 20, message = "Tag must be between 1 and 20 characters") String> tags;
}
