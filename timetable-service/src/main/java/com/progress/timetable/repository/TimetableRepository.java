package com.progress.timetable.repository;

import com.progress.timetable.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

}
