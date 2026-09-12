package com.example.wordassistant.repository;

import com.example.wordassistant.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByBookIdOrderByIdAsc(Long bookId);

    long countByBookId(Long bookId);

    @Modifying
    @Transactional
    void deleteByBookId(Long bookId);
}
