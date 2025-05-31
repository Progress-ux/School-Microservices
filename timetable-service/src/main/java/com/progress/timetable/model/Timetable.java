package com.progress.timetable.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "timetables")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long school_id;

    @Column(nullable = false)
    private Long teacher_id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime start_time;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime  end_time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Days day_of_week;

    @Column(nullable = false)
    private Integer max_students;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Timetable() {}

    public Long getId() { return id; }

    public Long getSchool_id() { return school_id; }
    public void setSchool_id(Long school_id) { this.school_id = school_id; }

    public Long getTeacher_id() { return teacher_id; }
    public void setTeacher_id(Long teacher_id) { this.teacher_id = teacher_id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalTime  getStart_time() { return start_time; }
    public void setStart_time(LocalTime  start_time) { this.start_time = start_time; }

    public LocalTime  getEnd_time() { return end_time; }
    public void setEnd_time(LocalTime  end_time) { this.end_time = end_time; }

    public Days getDay_of_week() { return day_of_week; }
    public void setDay_of_week(Days day_of_week) { this.day_of_week = day_of_week; }

    public Integer getMax_students() { return max_students; }
    public void setMax_students(Integer max_students) { this.max_students = max_students; }

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = created_at;
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }
}
