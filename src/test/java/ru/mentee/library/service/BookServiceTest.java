/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.CreateBookRequest;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.repository.BookRepository;
import ru.mentee.library.service.impl.BookServiceImpl;
import ru.mentee.library.service.validation.IsbnValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock private BookRepository bookRepository;

    @Mock private IsbnValidator isbnValidator;

    @InjectMocks private BookServiceImpl bookService;

    private CreateBookRequest createBookRequest;
    private Book book;

    @BeforeEach
    void setUp() {
        createBookRequest = new CreateBookRequest();
        createBookRequest.setTitle("Test Book");
        createBookRequest.setAuthor("Test Author");
        createBookRequest.setIsbn("978-3-16-148410-0");
        createBookRequest.setPublishedDate(LocalDate.of(2024, 1, 1));

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("978-3-16-148410-0");
        book.setPublishedDate(LocalDate.of(2024, 1, 1));
        book.setAvailable(true);
    }

    @Test
    @DisplayName("Should create book with valid data")
    void shouldCreateBookWithValidData() {
        // Given
        when(isbnValidator.isValid(anyString())).thenReturn(true);
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        BookDto result = bookService.createBook(createBookRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when ISBN is invalid")
    void shouldThrowExceptionWhenIsbnIsInvalid() {
        // Given
        when(isbnValidator.isValid(anyString())).thenReturn(false);

        // When & Then
        assertThrows(
                IllegalArgumentException.class, () -> bookService.createBook(createBookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when ISBN already exists")
    void shouldThrowExceptionWhenIsbnAlreadyExists() {
        // Given
        when(isbnValidator.isValid(anyString())).thenReturn(true);
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(
                IllegalArgumentException.class, () -> bookService.createBook(createBookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should get book by id")
    void shouldGetBookById() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // When
        BookDto result = bookService.getBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> bookService.getBookById(1L));
    }
}
