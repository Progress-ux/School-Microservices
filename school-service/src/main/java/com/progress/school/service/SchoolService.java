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

    public void updateSchool(Long id, CreateRequest request)
    {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Школа не найдена"));

        if(request.getName() != null) school.setName(request.getName());
        if(request.getAddress() != null) school.setAddress(request.getAddress());

        schoolRepository.save(school);
    }
}
