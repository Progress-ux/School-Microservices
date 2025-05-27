package com.progress.school.repository;

import com.progress.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findById(Long id);
    boolean existsByName(String name);
    Optional<School> findByName(String name);

}
