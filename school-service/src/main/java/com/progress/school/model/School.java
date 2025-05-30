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

    public School() {}

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

//    public Set<Long> getTeachersId() { return teachersId; }
//    public void setTeachersId(Set<Long> teachersId) { this.teachersId = teachersId; }

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

