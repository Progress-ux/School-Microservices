package com.progress.timetable.service;

import com.progress.timetable.dto.CreateRequest;
import com.progress.timetable.dto.CreateTimetableBookRequest;
import com.progress.timetable.model.Timetable;
import com.progress.timetable.model.TimetableBookings;
import com.progress.timetable.repository.TimetableBookRepository;
import com.progress.timetable.repository.TimetableRepository;
import org.springframework.stereotype.Service;

@Service
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final TimetableBookRepository timetableBookRepository;

    public TimetableService(TimetableRepository timetableRepository,
                            TimetableBookRepository timetableBookRepository)
    {
        this.timetableRepository = timetableRepository;
        this.timetableBookRepository = timetableBookRepository;
    }

    /**
     * Создание нового расписания.
     * @param request объект с данными.
     */
    public void createTimetable(CreateRequest request)
    {
        Timetable timetable = new Timetable();
        timetable.setSchoolId(request.getSchoolId());
        timetable.setTeacherId(request.getTeacherId());

        timetable.setSubject(request.getSubject());

        timetable.setStart_time(request.getStart_time());
        timetable.setEnd_time(request.getEnd_time());

        timetable.setDay_of_week(request.getDay_of_week());

        timetable.setMax_students(request.getMax_students());

        timetableRepository.save(timetable);
    }

    /**
     * Создание записи студента.
     * @param timetable_id ID расписания.
     * @param student_id ID студента.
     */
    public void createTimetableBook(Long timetable_id, Long student_id)
    {
        TimetableBookings timetableBookings = new TimetableBookings();

        timetableBookings.setTimetable_id(timetable_id);
        timetableBookings.setStudent_id(student_id);

        timetableBookRepository.save(timetableBookings);
    }

    /**
     * Обновление данных расписания.
     * @param id ID расписания.
     * @param request Объект с обновленными данными.
     */
    public void updateTimetable(Long id, CreateRequest request)
    {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));

        if(request.getSchoolId() != null) timetable.setSchoolId(request.getSchoolId());
        if(request.getTeacherId() != null) timetable.setTeacherId(request.getTeacherId());

        if(request.getSubject() != null) timetable.setSubject(request.getSubject());

        if(request.getStart_time() != null) timetable.setStart_time(request.getStart_time());
        if(request.getEnd_time() != null) timetable.setEnd_time(request.getEnd_time());

        if(request.getDay_of_week() != null) timetable.setDay_of_week(request.getDay_of_week());

        if(request.getMax_students() != null) timetable.setMax_students(request.getMax_students());

        timetableRepository.save(timetable);
    }
}
