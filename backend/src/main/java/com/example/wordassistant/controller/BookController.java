package com.example.wordassistant.controller;

import com.example.wordassistant.entity.Word;
import com.example.wordassistant.entity.WordBook;
import com.example.wordassistant.repository.WordBookRepository;
import com.example.wordassistant.repository.WordRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词书管理: 用户导入的词书连同单词一起存入数据库,
 * 登录后多端同步; 游客模式仍由前端 localStorage 承载。
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final int MAX_BOOKS_PER_USER = 50;
    private static final int MAX_WORDS_PER_BOOK = 10000;

    private final WordBookRepository bookRepo;
    private final WordRepository wordRepo;

    public BookController(WordBookRepository bookRepo, WordRepository wordRepo) {
        this.bookRepo = bookRepo;
        this.wordRepo = wordRepo;
    }

    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("userId");
    }

    private WordBook ownedBook(Long bookId, Long userId) {
        return bookRepo.findById(bookId)
                .filter(b -> b.getUserId().equals(userId))
                .orElse(null);
    }

    /** 词书列表(含单词数, 不含单词明细) */
    @GetMapping
    public List<Map<String, Object>> listBooks(HttpServletRequest request) {
        Long uid = currentUserId(request);
        List<WordBook> books = bookRepo.findByUserIdOrderByIsPinnedDescCreatedAtDesc(uid);
        List<Map<String, Object>> result = new ArrayList<>();
        for (WordBook b : books) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", b.getId());
            item.put("name", b.getName());
            item.put("isPinned", b.getIsPinned());
            item.put("createdAt", b.getCreatedAt());
            item.put("wordCount", wordRepo.countByBookId(b.getId()));
            result.add(item);
        }
        return result;
    }

    /** 单本词书(含全部单词) */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id, HttpServletRequest request) {
        Long uid = currentUserId(request);
        WordBook book = ownedBook(id, uid);
        if (book == null) {
            return ResponseEntity.status(404).body(Map.of("message", "词书不存在"));
        }
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", book.getId());
        dto.put("name", book.getName());
        dto.put("isPinned", book.getIsPinned());
        dto.put("createdAt", book.getCreatedAt());
        dto.put("words", wordRepo.findByBookIdOrderByIdAsc(id));
        return ResponseEntity.ok(dto);
    }

    /** 新建词书: {name, words: [{word, phonetic, definition, unit}]} */
    @PostMapping
    @Transactional
    public ResponseEntity<?> createBook(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long uid = currentUserId(request);
        String name = payload.get("name") == null ? "" : String.valueOf(payload.get("name")).trim();
        if (name.isEmpty() || name.length() > 100) {
            return ResponseEntity.badRequest().body(Map.of("message", "词书名称不能为空且不超过 100 字"));
        }
        if (bookRepo.findByUserIdOrderByIsPinnedDescCreatedAtDesc(uid).size() >= MAX_BOOKS_PER_USER) {
            return ResponseEntity.badRequest().body(Map.of("message", "词书数量已达上限 (" + MAX_BOOKS_PER_USER + " 本)"));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawWords = (List<Map<String, Object>>) payload.get("words");
        if (rawWords == null || rawWords.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "词书不能为空"));
        }
        if (rawWords.size() > MAX_WORDS_PER_BOOK) {
            return ResponseEntity.badRequest().body(Map.of("message", "单本词书最多 " + MAX_WORDS_PER_BOOK + " 个单词"));
        }

        WordBook book = new WordBook();
        book.setName(name);
        book.setUserId(uid);
        book = bookRepo.save(book);

        List<Word> words = new ArrayList<>(rawWords.size());
        for (Map<String, Object> raw : rawWords) {
            String wordText = raw.get("word") == null ? "" : String.valueOf(raw.get("word")).trim();
            if (wordText.isEmpty()) continue;
            Word w = new Word();
            w.setBookId(book.getId());
            w.setWord(wordText);
            w.setPhonetic(raw.get("phonetic") == null ? "" : String.valueOf(raw.get("phonetic")));
            w.setDefinition(raw.get("definition") == null ? "暂无释义" : String.valueOf(raw.get("definition")));
            w.setUnit(raw.get("unit") == null ? "默认分类" : String.valueOf(raw.get("unit")));
            words.add(w);
        }
        wordRepo.saveAll(words);

        return ResponseEntity.ok(Map.of("message", "词书保存成功", "id", book.getId(), "wordCount", words.size()));
    }

    /** 更新词书: {name?, isPinned?} */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long uid = currentUserId(request);
        WordBook book = ownedBook(id, uid);
        if (book == null) {
            return ResponseEntity.status(404).body(Map.of("message", "词书不存在"));
        }
        if (payload.containsKey("name")) {
            String name = String.valueOf(payload.get("name")).trim();
            if (name.isEmpty() || name.length() > 100) {
                return ResponseEntity.badRequest().body(Map.of("message", "词书名称不能为空且不超过 100 字"));
            }
            book.setName(name);
        }
        if (payload.containsKey("isPinned")) {
            book.setIsPinned(Boolean.TRUE.equals(payload.get("isPinned")));
        }
        bookRepo.save(book);
        return ResponseEntity.ok(Map.of("message", "词书更新成功"));
    }

    /** 删除词书及其全部单词 */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteBook(@PathVariable Long id, HttpServletRequest request) {
        Long uid = currentUserId(request);
        WordBook book = ownedBook(id, uid);
        if (book == null) {
            return ResponseEntity.status(404).body(Map.of("message", "词书不存在"));
        }
        wordRepo.deleteByBookId(id);
        bookRepo.delete(book);
        return ResponseEntity.ok(Map.of("message", "词书删除成功"));
    }
}
