package com.progress.school.repository;

import com.progress.school.dto.StudentInfo;
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

    @Query(value = "SELECT users.email as email, users.first_name as first_name, users.last_name as last_name " +
            "FROM users " +
            "JOIN school_teachers ON users.id = school_teachers.teacher_id " +
            "WHERE school_teachers.school_id = :school_id",
            nativeQuery = true
    )
    List<TeacherInfo> findTeacherInfoBySchoolId(@Param("school_id") Long schoolId);

    @Query(value = "SELECT users.email as email, users.first_name as first_name, users.last_name as last_name " +
            "FROM users " +
            "JOIN school_students ON users.id = school_students.student_id " +
            "WHERE school_students.school_id = :school_id",
            nativeQuery = true
    )
    List<StudentInfo> findStudentInfoBySchoolId(@Param("school_id") Long schoolId);

    @Query(value = "SELECT COUNT(*) > 0 FROM school_teachers WHERE school_id = :schoolId AND teacher_id = :teacherId",
            nativeQuery = true)
    boolean existsBySchoolIdAndTeacherId(@Param("schoolId") Long schoolId, @Param("teacherId") Long teacherId);

}
