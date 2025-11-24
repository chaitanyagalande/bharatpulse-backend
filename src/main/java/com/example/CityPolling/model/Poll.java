package com.example.CityPolling.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "poll")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question is required")
    @Size(min = 5, max = 255, message = "Question must be between 5 and 255 characters")
    @Column(name = "question", nullable = false, length = 255)
    private String question;

    @NotBlank(message = "Option 1 is required")
    @Size(max = 100, message = "Option 1 must not exceed 100 characters")
    @Column(name = "option_one", nullable = false, length = 100)
    private String optionOne;

    @NotBlank(message = "Option 2 is required")
    @Size(max = 100, message = "Option 2 must not exceed 100 characters")
    @Column(name = "option_two", nullable = false, length = 100)
    private String optionTwo;

    @Size(max = 100, message = "Option 3 must not exceed 100 characters")
    @Column(name = "option_three", length = 100)
    private String optionThree;

    @Size(max = 100, message = "Option 4 must not exceed 100 characters")
    @Column(name = "option_four", length = 100)
    private String optionFour;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // New fields for real-time vote counts
    @Column(name = "option_one_votes", nullable = false)
    private Long optionOneVotes = 0L;

    @Column(name = "option_two_votes", nullable = false)
    private Long optionTwoVotes = 0L;

    @Column(name = "option_three_votes")
    private Long optionThreeVotes = 0L;

    @Column(name = "option_four_votes")
    private Long optionFourVotes = 0L;
}