package com.progress.school.repository;

import com.progress.school.dto.TeacherInfo;
import com.progress.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolTeacherRepository extends JpaRepository<School, Long> {
    Optional<School> findById(Long id);

    @Query(value = "SELECT u " +
            "FROM users u " +
            "JOIN school_teachers st ON u.id = st.teacherId " +
            "WHERE st.schoolId = :schoolId",
            nativeQuery = true
    )
    List<TeacherInfo> findTeacherInfoBySchoolId(@Param("schoolId") Long schoolId);
}
