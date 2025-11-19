package com.example.CityPolling.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "POLL_TAG")

// This connects Poll ↔ Tag (many-to-many through mapping entity).
public class PollTag {

    @EmbeddedId
    private PollTagId id;

    @ManyToOne
    @MapsId("pollId")
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
