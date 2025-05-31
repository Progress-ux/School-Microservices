package com.progress.timetable.dto;

public class CreateTimetableBookRequest {
    private Long timetable_id;
    private Long student_id;

    public Long getTimetable_id() { return timetable_id; }
    public void setTimetable_id(Long timetable_id) { this.timetable_id = timetable_id; }

    public Long getStudent_id() { return student_id; }
    public void setStudent_id(Long student_id) { this.student_id = student_id; }
}
