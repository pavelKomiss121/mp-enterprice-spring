/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.BookSearchCriteria;
import ru.mentee.library.service.BookSearchService;

/**
 * REST API для продвинутого поиска книг.
 */
@RestController
@RequestMapping("/api/v1/books/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    /**
     * Продвинутый поиск книг по множеству критериев.
     *
     * @param title часть названия книги
     * @param author имя или фамилия автора
     * @param categoryId ID категории
     * @param fromYear год публикации, от
     * @param toYear год публикации, до
     * @param minPages мин. количество страниц
     * @param maxPages макс. количество страниц
     * @param available только доступные для выдачи
     * @param pageable параметры пагинации и сортировки
     * @return страница с найденными книгами
     */
    @GetMapping
    public ResponseEntity<Page<BookDto>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear,
            @RequestParam(required = false) Integer minPages,
            @RequestParam(required = false) Integer maxPages,
            @RequestParam(required = false) Boolean available,
            @PageableDefault(sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {

        BookSearchCriteria criteria =
                BookSearchCriteria.builder()
                        .title(title)
                        .author(author)
                        .categoryId(categoryId)
                        .fromYear(fromYear)
                        .toYear(toYear)
                        .minPages(minPages)
                        .maxPages(maxPages)
                        .available(available)
                        .build();

        Page<BookDto> books = bookSearchService.searchBooks(criteria, pageable);

        return ResponseEntity.ok(books);
    }
}
