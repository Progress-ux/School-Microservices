package com.progress.timetable.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.progress.timetable.model.Days;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public class CreateRequest {
    private Long school_id;
    private Long teacher_id;

    private String subject;

    @Schema(type = "string", example = "08:30", pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime start_time;

    @Schema(type = "string", example = "09:00", pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime end_time;

    private Days day_of_week;

    private Integer max_students;

    public CreateRequest() {}

    public Long getSchool_id() { return school_id; }
    public void setSchool_id(Long school_id) { this.school_id = school_id; }

    public Long getTeacher_id() { return teacher_id; }
    public void setTeacher_id(Long teacher_id) { this.teacher_id = teacher_id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalTime getStart_time() { return start_time; }
    public void setStart_time(LocalTime start_time) { this.start_time = start_time; }

    public LocalTime getEnd_time() { return end_time; }
    public void setEnd_time(LocalTime end_time) { this.end_time = end_time; }

    public Days getDay_of_week() { return day_of_week; }
    public void setDay_of_week(Days day_of_week) { this.day_of_week = day_of_week; }

    public Integer getMax_students() { return max_students; }
    public void setMax_students(Integer max_students) { this.max_students = max_students; }
}
