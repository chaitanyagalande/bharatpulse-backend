package com.example.CityPolling.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "tag",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "city"})  // ensures unique tag per city
        }
)

// Represents one unique tag (per city).
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)  // ✅ explicit lowercase
    private String name;

    @Column(name = "city", nullable = false, length = 100)  // ✅ explicit lowercase
    private String city;

    @Column(name = "usage_count", nullable = false)  // ✅ snake_case for column name
    private Long usageCount = 0L;
}
