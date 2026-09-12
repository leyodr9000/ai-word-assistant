package com.example.wordassistant.repository;

import com.example.wordassistant.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    Optional<StudyRecord> findByUserIdAndStudyDate(Long userId, LocalDate studyDate);

    /** 某用户全部历史学习秒数合计 */
    @Query("select coalesce(sum(r.seconds), 0) from StudyRecord r where r.userId = :userId")
    long sumSecondsByUserId(@Param("userId") Long userId);
}
