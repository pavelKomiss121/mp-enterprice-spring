/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.library.domain.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // ============================================
    // Derived Query Methods
    // ============================================

    // Поиск по имени
    List<Author> findByFirstName(String firstName);

    // Поиск по фамилии
    List<Author> findByLastName(String lastName);

    // Поиск по имени и фамилии
    List<Author> findByFirstNameAndLastName(String firstName, String lastName);

    // Поиск по имени или фамилии (частичное совпадение)
    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    // ============================================
    // @Query Methods - Сложные запросы
    // ============================================

    // Авторы с количеством книг
    @Query(
            """
        SELECT a.id as id,
               a.firstName as firstName,
               a.lastName as lastName,
               COUNT(b.id) as bookCount
        FROM Author a
        LEFT JOIN a.books b
        GROUP BY a.id, a.firstName, a.lastName
        ORDER BY COUNT(b.id) DESC
        """)
    List<AuthorWithBookCount> findAuthorsWithBookCount();

    // Авторы определенной категории
    @Query(
            """
        SELECT DISTINCT a FROM Author a
        JOIN a.books b
        WHERE b.category.id = :categoryId
        """)
    List<Author> findAuthorsByCategory(@Param("categoryId") Long categoryId);

    // Авторы, у которых есть книги в заданном статусе
    @Query(
            """
        SELECT DISTINCT a FROM Author a
        JOIN a.books b
        WHERE b.status = :status
        """)
    List<Author> findAuthorsByBookStatus(
            @Param("status") ru.mentee.library.domain.model.BookStatus status);

    // Поиск автора с его книгами (JOIN FETCH)
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :authorId")
    Author findByIdWithBooks(@Param("authorId") Long authorId);

    // ============================================
    // Projections
    // ============================================

    // Projection для автора с количеством книг
    interface AuthorWithBookCount {
        Long getId();

        String getFirstName();

        String getLastName();

        Long getBookCount();
    }

    // Summary projection
    interface AuthorSummary {
        Long getId();

        String getFirstName();

        String getLastName();
    }

    List<AuthorSummary> findAllBy();
}
