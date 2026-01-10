/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.BookSearchCriteria;
import ru.mentee.library.api.mapper.BookMapper;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.repository.BookRepository;
import ru.mentee.library.domain.specification.BookSpecifications;

/**
 * Сервис для продвинутого поиска книг с использованием Spring Data Specifications.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchService {

    private final BookRepository bookRepository;

    /**
     * Выполняет динамический поиск книг по заданным критериям.
     *
     * @param criteria критерии поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница с найденными книгами
     */
    public Page<BookDto> searchBooks(BookSearchCriteria criteria, Pageable pageable) {
        // Строим динамическую спецификацию из критериев
        Specification<Book> spec = buildSpecification(criteria);

        // Выполняем запрос с пагинацией
        Page<Book> booksPage = bookRepository.findAll(spec, pageable);

        // Преобразуем Book сущности в BookDto
        return booksPage.map(BookMapper::toDto);
    }

    /**
     * Строит Specification из критериев поиска.
     * Комбинирует все непустые критерии через AND.
     *
     * @param criteria критерии поиска
     * @return скомбинированная спецификация
     */
    private Specification<Book> buildSpecification(BookSearchCriteria criteria) {
        return Specification.where(BookSpecifications.titleContains(criteria.getTitle()))
                .and(BookSpecifications.authorNameContains(criteria.getAuthor()))
                .and(BookSpecifications.inCategory(criteria.getCategoryId()))
                .and(
                        BookSpecifications.publishedBetween(
                                criteria.getFromYear(), criteria.getToYear()))
                .and(
                        BookSpecifications.pagesBetween(
                                criteria.getMinPages(), criteria.getMaxPages()))
                .and(BookSpecifications.availableOnly(criteria.getAvailable()))
                // FETCH JOIN для оптимизации (избегаем N+1)
                .and(BookSpecifications.fetchCategory())
                .and(BookSpecifications.fetchAuthors());
    }
}
