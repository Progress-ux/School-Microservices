package com.progress.school.repository;

import com.progress.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findById(Long id);
    boolean existsByName(String name);
    Optional<School> findByName(String name);
}
