package com.progress.timetable.repository;

import com.progress.timetable.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findAllBySchoolId(Long school_id);
    List<Timetable> findAllByTeacherId(Long teacher_id);
}
