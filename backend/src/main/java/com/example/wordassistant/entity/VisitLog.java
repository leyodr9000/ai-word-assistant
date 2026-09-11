package com.example.wordassistant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_log")
public class VisitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime visitTime;
    
    @PrePersist
    protected void onCreate() {
        visitTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getVisitTime() { return visitTime; }
    public void setVisitTime(LocalDateTime visitTime) { this.visitTime = visitTime; }
}
