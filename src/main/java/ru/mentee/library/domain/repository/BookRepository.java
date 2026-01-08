/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.library.domain.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE (:author IS NULL OR b.author = :author)")
    List<Book> findByAuthor(@Param("author") String author);

    List<Book> findByAuthorContainingIgnoreCase(String author);
}
