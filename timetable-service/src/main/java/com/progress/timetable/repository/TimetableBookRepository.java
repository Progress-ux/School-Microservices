package com.progress.timetable.repository;

import com.progress.timetable.model.TimetableBookings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableBookRepository extends JpaRepository<TimetableBookings, Long> {
}
