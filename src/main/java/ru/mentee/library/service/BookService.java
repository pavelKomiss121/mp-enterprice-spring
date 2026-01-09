/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.service;

import java.util.List;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.CreateBookRequest;

public interface BookService {

    BookDto createBook(CreateBookRequest request);

    BookDto getBookById(Long id);

    List<BookDto> getAllBooks(String author);

    BookDto updateBook(Long id, CreateBookRequest request);

    void deleteBook(Long id);
}
