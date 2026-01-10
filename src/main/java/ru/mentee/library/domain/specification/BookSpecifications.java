/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.mentee.library.domain.model.Author;
import ru.mentee.library.domain.model.Author_;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.model.BookStatus;
import ru.mentee.library.domain.model.Book_;
import ru.mentee.library.domain.model.Category_;

/**
 * Спецификации для динамической фильтрации книг.
 * Каждый метод возвращает переиспользуемую Specification для одного критерия.
 */
public class BookSpecifications {

    /**
     * Поиск по названию книги (частичное совпадение, регистронезависимо).
     *
     * @param title часть названия
     * @return спецификация для фильтрации по названию
     */
    public static Specification<Book> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank()) {
                return criteriaBuilder.conjunction(); // WHERE 1=1 (всегда true)
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Book_.title)), "%" + title.toLowerCase() + "%");
        };
    }

    /**
     * Поиск по имени или фамилии автора (частичное совпадение, регистронезависимо).
     *
     * @param authorName имя или фамилия автора
     * @return спецификация для фильтрации по автору
     */
    public static Specification<Book> authorNameContains(String authorName) {
        return (root, query, criteriaBuilder) -> {
            if (authorName == null || authorName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            // DISTINCT для избежания дубликатов при JOIN
            query.distinct(true);

            // JOIN с таблицей authors через ManyToMany связь
            Join<Book, Author> authorsJoin = root.join(Book_.authors, JoinType.LEFT);

            // Ищем либо по firstName, либо по lastName
            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(authorsJoin.get(Author_.firstName)),
                            "%" + authorName.toLowerCase() + "%"),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(authorsJoin.get(Author_.lastName)),
                            "%" + authorName.toLowerCase() + "%"));
        };
    }

    /**
     * Фильтрация по категории.
     *
     * @param categoryId ID категории
     * @return спецификация для фильтрации по категории
     */
    public static Specification<Book> inCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            // ManyToOne связь с Category
            return criteriaBuilder.equal(root.get(Book_.category).get(Category_.id), categoryId);
        };
    }

    /**
     * Фильтрация по диапазону годов публикации.
     *
     * @param fromYear минимальный год (включительно)
     * @param toYear максимальный год (включительно)
     * @return спецификация для фильтрации по годам
     */
    public static Specification<Book> publishedBetween(Integer fromYear, Integer toYear) {
        return (root, query, criteriaBuilder) -> {
            if (fromYear == null && toYear == null) {
                return criteriaBuilder.conjunction();
            }

            if (fromYear == null) {
                // Только toYear
                return criteriaBuilder.lessThanOrEqualTo(root.get(Book_.publicationYear), toYear);
            }

            if (toYear == null) {
                // Только fromYear
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.get(Book_.publicationYear), fromYear);
            }

            // Оба значения
            return criteriaBuilder.between(root.get(Book_.publicationYear), fromYear, toYear);
        };
    }

    /**
     * Фильтрация по диапазону количества страниц.
     *
     * @param minPages минимальное количество страниц
     * @param maxPages максимальное количество страниц
     * @return спецификация для фильтрации по страницам
     */
    public static Specification<Book> pagesBetween(Integer minPages, Integer maxPages) {
        return (root, query, criteriaBuilder) -> {
            if (minPages == null && maxPages == null) {
                return criteriaBuilder.conjunction();
            }

            if (minPages == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Book_.pages), maxPages);
            }

            if (maxPages == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Book_.pages), minPages);
            }

            return criteriaBuilder.between(root.get(Book_.pages), minPages, maxPages);
        };
    }

    /**
     * Фильтрация по статусу доступности (только AVAILABLE книги).
     *
     * @param availableOnly если true, то только доступные книги
     * @return спецификация для фильтрации по статусу
     */
    public static Specification<Book> availableOnly(Boolean availableOnly) {
        return (root, query, criteriaBuilder) -> {
            if (availableOnly == null || !availableOnly) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Book_.status), BookStatus.AVAILABLE);
        };
    }

    /**
     * FETCH JOIN для категории, чтобы избежать N+1 проблемы.
     *
     * @return спецификация с FETCH JOIN категории
     */
    public static Specification<Book> fetchCategory() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch(Book_.category, JoinType.LEFT);
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * FETCH JOIN для авторов, чтобы избежать N+1 проблемы.
     *
     * @return спецификация с FETCH JOIN авторов
     */
    public static Specification<Book> fetchAuthors() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
                root.fetch(Book_.authors, JoinType.LEFT);
            }
            return criteriaBuilder.conjunction();
        };
    }
}
