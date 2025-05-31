package com.progress.school.model;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "schools")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;


    @ElementCollection
    @CollectionTable(name = "school_teachers", joinColumns = @JoinColumn(name = "school_id"))
    @Column(name = "teacher_id")
    private Set<Long> teacherIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "school_students", joinColumns = @JoinColumn(name = "school_id"))
    @Column(name = "student_id")
    private Set<Long> studentIds = new HashSet<>();

    public School() {}

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Set<Long> getTeachersIds() { return teacherIds; }
    public void setTeachersIds(Set<Long> teacherIds) { this.teacherIds = teacherIds; }

    public Set<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(Set<Long> studentIds) { this.studentIds = studentIds; }

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

