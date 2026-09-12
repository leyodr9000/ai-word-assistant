package com.example.wordassistant.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日学习时长记录 (按 用户 + 日期 唯一)。
 * 前端每隔一段时间把当日本地累计的增量上报, 服务端做累加。
 */
@Entity
@Table(name = "study_record", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "study_date"})
})
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    /** 当日累计有效学习秒数 */
    @Column(nullable = false)
    private Integer seconds;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getStudyDate() { return studyDate; }
    public void setStudyDate(LocalDate studyDate) { this.studyDate = studyDate; }
    public Integer getSeconds() { return seconds; }
    public void setSeconds(Integer seconds) { this.seconds = seconds; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
