package com.example.wordassistant.repository;

import com.example.wordassistant.entity.WordBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordBookRepository extends JpaRepository<WordBook, Long> {

    /** 按用户取词书: 置顶优先, 其余按导入时间倒序 */
    List<WordBook> findByUserIdOrderByIsPinnedDescCreatedAtDesc(Long userId);
}
