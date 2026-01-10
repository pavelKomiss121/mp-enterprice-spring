/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.model.BookStatus;
import ru.mentee.library.domain.model.Category;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // ============================================
    // Derived Query Methods
    // ============================================

    // Поиск по ISBN
    Optional<Book> findByIsbn(String isbn);

    // Поиск по названию (частичное совпадение, игнорируя регистр)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Фильтрация по статусу
    List<Book> findByStatus(BookStatus status);

    // Фильтрация по статусу с пагинацией
    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    // Фильтрация по категории
    List<Book> findByCategory(Category category);

    // Фильтрация по категории и статусу
    Page<Book> findByCategoryAndStatus(Category category, BookStatus status, Pageable pageable);

    // Фильтрация по году публикации
    List<Book> findByPublicationYear(Integer year);

    // Фильтрация по диапазону лет
    List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);

    // ============================================
    // @Query Methods - Сложные запросы
    // ============================================

    // Поиск книг по фамилии автора
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE a.lastName = :lastName")
    List<Book> findByAuthorsLastName(@Param("lastName") String lastName);

    // Поиск книг по имени и фамилии автора
    @Query(
            "SELECT DISTINCT b FROM Book b JOIN b.authors a "
                    + "WHERE a.firstName = :firstName AND a.lastName = :lastName")
    List<Book> findByAuthorFullName(
            @Param("firstName") String firstName, @Param("lastName") String lastName);

    // Поиск книг с просроченным возвратом
    @Query(
            """
        SELECT DISTINCT b FROM Book b
        JOIN Loan l ON l.book = b
        WHERE l.status = 'OVERDUE'
        """)
    List<Book> findBooksWithOverdueLoans();

    // Проверка доступности книги (нет активных выдач)
    @Query(
            """
        SELECT CASE WHEN COUNT(l) = 0 THEN true ELSE false END
        FROM Loan l
        WHERE l.book.id = :bookId
        AND l.status = 'ACTIVE'
        """)
    boolean isBookAvailable(@Param("bookId") Long bookId);

    // Топ популярных книг (по количеству выдач)
    @Query(
            """
        SELECT b FROM Book b
        LEFT JOIN Loan l ON l.book = b
        GROUP BY b.id
        ORDER BY COUNT(l) DESC
        """)
    List<Book> findTopPopularBooks(Pageable pageable);

    // Поиск книг по категории с JOIN FETCH для оптимизации
    @Query(
            "SELECT DISTINCT b FROM Book b "
                    + "LEFT JOIN FETCH b.authors "
                    + "WHERE b.category.id = :categoryId "
                    + "AND b.status = :status")
    List<Book> findByCategoryIdAndStatusWithAuthors(
            @Param("categoryId") Long categoryId, @Param("status") BookStatus status);

    // Количество книг в категории
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    // ============================================
    // Projections
    // ============================================

    // Interface-based projection для списка книг
    interface BookSummary {
        Long getId();

        String getIsbn();

        String getTitle();

        Integer getPublicationYear();

        BookStatus getStatus();

        String getCategoryName();
    }

    // Projection с именованным методом
    @Query(
            """
        SELECT b.id as id,
               b.isbn as isbn,
               b.title as title,
               b.publicationYear as publicationYear,
               b.status as status,
               c.name as categoryName
        FROM Book b
        LEFT JOIN b.category c
        WHERE b.status = :status
        """)
    List<BookSummary> findBookSummariesByStatus(@Param("status") BookStatus status);

    // Projection для деталей книги с авторами
    interface BookDetails {
        Long getId();

        String getIsbn();

        String getTitle();

        String getDescription();

        Integer getPublicationYear();

        Integer getPages();

        BookStatus getStatus();

        Category getCategory();

        // Вложенная проекция для авторов
        interface AuthorInfo {
            String getFirstName();

            String getLastName();
        }

        List<AuthorInfo> getAuthors();
    }

    // Dynamic projection
    <T> List<T> findByStatus(BookStatus status, Class<T> type);
}
