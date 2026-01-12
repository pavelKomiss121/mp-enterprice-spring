/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.mapper.BookMapper;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.repository.BookRepository;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping
    public ResponseEntity<Page<BookDto>> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return ResponseEntity.ok(books.map(BookMapper::toDto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        // Минимальная реализация для тестирования безопасности
        return ResponseEntity.ok(bookDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        // Минимальная реализация для тестирования безопасности
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        // Минимальная реализация для тестирования безопасности
        return ResponseEntity.noContent().build();
    }
}
