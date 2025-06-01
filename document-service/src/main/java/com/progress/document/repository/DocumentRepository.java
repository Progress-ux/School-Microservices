package com.progress.document.repository;

import com.progress.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByUserId(Long user_id);
    List<Document> findAllBySchoolId(Long school_id);
    Optional<Document> findByUserIdAndDate(Long userId, LocalDate date);
}
