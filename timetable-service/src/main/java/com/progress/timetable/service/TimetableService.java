package com.progress.timetable.service;

import com.progress.timetable.dto.CreateRequest;
import com.progress.timetable.model.Timetable;
import com.progress.timetable.repository.TimetableRepository;
import org.springframework.stereotype.Service;

@Service
public class TimetableService {
    private final TimetableRepository timetableRepository;

    public TimetableService(TimetableRepository timetableRepository)
    {
        this.timetableRepository = timetableRepository;
    }

    public void createTimetable(CreateRequest request)
    {
        Timetable timetable = new Timetable();
        timetable.setSchool_id(request.getSchool_id());
        timetable.setTeacher_id(request.getTeacher_id());

        timetable.setSubject(request.getSubject());

        timetable.setStart_time(request.getStart_time());
        timetable.setEnd_time(request.getEnd_time());

        timetable.setDay_of_week(request.getDay_of_week());

        timetable.setMax_students(request.getMax_students());

        timetableRepository.save(timetable);
    }

    public void updateTimetable(Long id, CreateRequest request)
    {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));

        if(request.getSchool_id() != null) timetable.setSchool_id(request.getSchool_id());
        if(request.getTeacher_id() != null) timetable.setTeacher_id(request.getTeacher_id());

        if(request.getSubject() != null) timetable.setSubject(request.getSubject());

        if(request.getStart_time() != null) timetable.setStart_time(request.getStart_time());
        if(request.getEnd_time() != null) timetable.setEnd_time(request.getEnd_time());

        if(request.getDay_of_week() != null) timetable.setDay_of_week(request.getDay_of_week());

        if(request.getMax_students() != null) timetable.setMax_students(request.getMax_students());

        timetableRepository.save(timetable);
    }
}
