package com.progress.document.service;

import com.progress.document.dto.CreateRequest;
import com.progress.document.model.Document;
import com.progress.document.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository)
    {
        this.documentRepository = documentRepository;
    }

    public void createDocument(CreateRequest request)
    {
        Document document = new Document();

        document.setUserId(request.getUserId());
        document.setSchoolId(request.getSchoolId());
        document.setTimetableId(request.getTimetableId());

        document.setDate(request.getDate());
        document.setStatus(request.getStatus());
        document.setNotes(request.getNotes());

        documentRepository.save(document);
    }

    public void updateDocument(Long id, CreateRequest request)
    {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));

        if(request.getUserId() != null) document.setUserId(request.getUserId());
        if(request.getSchoolId() != null) document.setSchoolId(request.getSchoolId());;
        if(request.getTimetableId() != null) document.setTimetableId(request.getTimetableId());

        if(request.getDate() != null) document.setDate(request.getDate());
        if(request.getStatus() != null) document.setStatus(request.getStatus());
        if(request.getNotes() != null) document.setNotes(request.getNotes());

        documentRepository.save(document);
    }
}
