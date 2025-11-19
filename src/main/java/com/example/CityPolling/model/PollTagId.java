package com.example.CityPolling.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable

// Primary Key for the Mapping Table (PollTag)
public class PollTagId implements Serializable {

    private Long pollId;
    private Long tagId;
}
