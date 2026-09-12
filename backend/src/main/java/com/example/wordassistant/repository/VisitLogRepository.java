package com.example.wordassistant.repository;

import com.example.wordassistant.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    /** 最近 N 次登录/访问记录 */
    List<VisitLog> findTop10ByOrderByVisitTimeDesc();
}
