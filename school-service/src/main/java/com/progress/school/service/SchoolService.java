package com.progress.school.service;

import com.progress.school.dto.CreateRequest;
import com.progress.school.model.School;
import com.progress.school.repository.SchoolRepository;
import org.springframework.stereotype.Service;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository)
    {
        this.schoolRepository = schoolRepository;
    }

    public void createSchool(CreateRequest request)
    {
        if(request.getName() == null)
        {
            throw new IllegalArgumentException("Название школы не может быть пустым");
        }

        School school = new School();
        school.setName(request.getName());
        school.setAddress(request.getAddress());

        schoolRepository.save(school);
    }
}
