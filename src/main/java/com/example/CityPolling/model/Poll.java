package com.example.CityPolling.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "POLL")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", nullable = false, length = 255)
    private String question;

    @Column(name = "option_one", nullable = false, length = 100)
    private String optionOne;

    @Column(name = "option_two", nullable = false, length = 100)
    private String optionTwo;

    @Column(name = "option_three", length = 100)
    private String optionThree;

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