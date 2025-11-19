package com.example.CityPolling.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "TAG",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "city"})  // ensures unique tag per city
        }
)

// Represents one unique tag (per city).
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false)
    private Long usageCount = 0L;
}
