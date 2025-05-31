package com.progress.timetable.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "timetable_bookings")
public class TimetableBookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long timetable_id;
    private Long student_id;

    private LocalDateTime booked_at;

    public TimetableBookings() {}

    public Long getId() { return id; }

    public Long getTimetable_id() { return timetable_id; }
    public void setTimetable_id(Long timetable_id) { this.timetable_id = timetable_id; }

    public Long getStudent_id() { return student_id; }
    public void setStudent_id(Long student_id) { this.student_id = student_id; }

    @PrePersist
    protected void onCreate()
    {
        this.booked_at = LocalDateTime.now();
    }
}
