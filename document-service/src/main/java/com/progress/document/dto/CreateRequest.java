package com.progress.document.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.progress.document.model.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class CreateRequest {
    private Long userId;
    private Long schoolId;
    private Long timetableId;

    private LocalDate date;
    private Status status;
    private String notes;

    public CreateRequest () {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(Long timetableId) {
        this.timetableId = timetableId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
