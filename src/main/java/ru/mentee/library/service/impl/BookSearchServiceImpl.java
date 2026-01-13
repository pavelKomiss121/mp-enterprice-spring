/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service.impl;

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
import ru.mentee.library.service.BookSearchService;

/**
 * Реализация сервиса для продвинутого поиска книг с использованием Spring Data Specifications.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchServiceImpl implements BookSearchService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookDto> searchBooks(BookSearchCriteria criteria, Pageable pageable) {
        Specification<Book> spec = buildSpecification(criteria);
        Page<Book> booksPage = bookRepository.findAll(spec, pageable);
        return booksPage.map(bookMapper::toDto);
    }

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
                .and(BookSpecifications.fetchCategory())
                .and(BookSpecifications.fetchAuthors());
    }
}
