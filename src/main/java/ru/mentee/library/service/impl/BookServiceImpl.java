/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.CreateBookRequest;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.repository.BookRepository;
import ru.mentee.library.service.BookService;
import ru.mentee.library.service.validation.IsbnValidator;

@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.beans.factory.annotation.Qualifier("bookService") public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final IsbnValidator isbnValidator;

    @PostConstruct
    public void init() {
        log.info("BookServiceImpl bean initialized");
    }

    @PreDestroy
    public void cleanup() {
        log.info("BookServiceImpl bean is being destroyed");
    }

    @Override
    @Transactional
    public BookDto createBook(CreateBookRequest request) {
        log.info("Creating book with title: {}", request.getTitle());

        if (!isbnValidator.isValid(request.getIsbn())) {
            throw new IllegalArgumentException("Invalid ISBN format: " + request.getIsbn());
        }

        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new IllegalArgumentException(
                    "Book with ISBN " + request.getIsbn() + " already exists");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());
        book.setAvailable(true);

        Book savedBook = bookRepository.save(book);
        log.info("Book created with ID: {}", savedBook.getId());

        return toDto(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        log.info("Getting book by ID: {}", id);
        Book book =
                bookRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getAllBooks(String author) {
        log.info("Getting all books, author filter: {}", author);
        List<Book> books;
        if (author != null && !author.isBlank()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else {
            books = bookRepository.findAll();
        }
        return books.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookDto updateBook(Long id, CreateBookRequest request) {
        log.info("Updating book with ID: {}", id);
        Book book =
                bookRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (!isbnValidator.isValid(request.getIsbn())) {
            throw new IllegalArgumentException("Invalid ISBN format: " + request.getIsbn());
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());

        Book updatedBook = bookRepository.save(book);
        log.info("Book updated with ID: {}", updatedBook.getId());

        return toDto(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Book deleted with ID: {}", id);
    }

    private BookDto toDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishedDate(book.getPublishedDate())
                .available(book.getAvailable())
                .build();
    }
}
