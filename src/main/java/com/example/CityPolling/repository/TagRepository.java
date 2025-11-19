package com.example.CityPolling.repository;

import com.example.CityPolling.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndCity(String name, String city);

    List<Tag> findByCityOrderByUsageCountDesc(String city);
}
