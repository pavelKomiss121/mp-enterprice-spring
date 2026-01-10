/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.library.config.TestJpaAuditConfig;
import ru.mentee.library.domain.model.*;

@Disabled("Отключено для ускорения тестов - используется только booking модуль")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestJpaAuditConfig.class)
class BookRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private BookRepository bookRepository;

    @Autowired private TestEntityManager entityManager;

    @Test
    @DisplayName("Should найти книги по автору")
    void shouldFindBooksByAuthor() {
        // Given
        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");
        entityManager.persist(author);

        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setStatus(BookStatus.AVAILABLE);
        book.getAuthors().add(author);
        entityManager.persistAndFlush(book);

        // When
        List<Book> books = bookRepository.findByAuthorsLastName("Doe");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    @DisplayName("Should поддерживать пагинацию")
    void shouldSupportPagination() {
        // Given - создаем 25 книг
        IntStream.rangeClosed(1, 25)
                .forEach(
                        i -> {
                            Book book = new Book();
                            book.setIsbn("ISBN" + i);
                            book.setTitle("Book " + i);
                            book.setStatus(BookStatus.AVAILABLE);
                            entityManager.persist(book);
                        });
        entityManager.flush();

        // When
        Page<Book> firstPage =
                bookRepository.findByStatus(
                        BookStatus.AVAILABLE, PageRequest.of(0, 10, Sort.by("title")));

        // Then
        assertThat(firstPage.getTotalElements()).isEqualTo(25);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(firstPage.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Should работать аудит")
    void shouldAuditCreation() {
        // Given
        Book book = new Book();
        book.setIsbn("9876543210");
        book.setTitle("Audited Book");
        book.setStatus(BookStatus.AVAILABLE);

        // When
        Book saved = bookRepository.save(book);
        entityManager.flush(); // Ensure auditing is triggered

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull(); // updatedAt also set on creation
        // Note: createdBy will be "test-user" from TestJpaAuditConfig
    }

    @Test
    @DisplayName("Should найти книгу по ISBN")
    void shouldFindBookByIsbn() {
        // Given
        Book book = new Book();
        book.setIsbn("1111111111");
        book.setTitle("ISBN Test");
        book.setStatus(BookStatus.AVAILABLE);
        entityManager.persistAndFlush(book);

        // When
        Optional<Book> found = bookRepository.findByIsbn("1111111111");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("ISBN Test");
    }

    @Test
    @DisplayName("Should найти книги по названию")
    void shouldFindBooksByTitleContaining() {
        // Given
        Book book1 = new Book();
        book1.setIsbn("ISBN1");
        book1.setTitle("Java Programming");
        book1.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book1);

        Book book2 = new Book();
        book2.setIsbn("ISBN2");
        book2.setTitle("Advanced Java");
        book2.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book2);

        Book book3 = new Book();
        book3.setIsbn("ISBN3");
        book3.setTitle("Python Basics");
        book3.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book3);

        entityManager.flush();

        // When
        List<Book> javaBooks = bookRepository.findByTitleContainingIgnoreCase("java");

        // Then
        assertThat(javaBooks).hasSize(2);
        assertThat(javaBooks)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Java Programming", "Advanced Java");
    }

    @Test
    @DisplayName("Should фильтровать по статусу")
    void shouldFilterByStatus() {
        // Given
        Book available = new Book();
        available.setIsbn("ISBN_AVAIL");
        available.setTitle("Available Book");
        available.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(available);

        Book loaned = new Book();
        loaned.setIsbn("ISBN_LOANED");
        loaned.setTitle("Loaned Book");
        loaned.setStatus(BookStatus.LOANED);
        entityManager.persist(loaned);

        entityManager.flush();

        // When
        List<Book> availableBooks = bookRepository.findByStatus(BookStatus.AVAILABLE);
        List<Book> loanedBooks = bookRepository.findByStatus(BookStatus.LOANED);

        // Then
        assertThat(availableBooks).hasSize(1);
        assertThat(loanedBooks).hasSize(1);
        assertThat(availableBooks.get(0).getTitle()).isEqualTo("Available Book");
    }

    @Test
    @DisplayName("Should проверить доступность книги")
    void shouldCheckBookAvailability() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("ISBN_AVAIL_CHECK");
        book.setTitle("Availability Test");
        book.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book);

        Loan activeLoan = new Loan();
        activeLoan.setBook(book);
        activeLoan.setUser(user);
        activeLoan.setLoanDate(Instant.now());
        activeLoan.setDueDate(Instant.now().plusSeconds(86400 * 14)); // 14 days
        activeLoan.setStatus(LoanStatus.ACTIVE);
        entityManager.persistAndFlush(activeLoan);

        // When
        boolean isAvailable = bookRepository.isBookAvailable(book.getId());

        // Then
        assertThat(isAvailable).isFalse();
    }

    @Test
    @DisplayName("Should найти топ популярных книг")
    void shouldFindTopPopularBooks() {
        // Given
        User user = new User();
        user.setEmail("reader@example.com");
        user.setFirstName("Reader");
        user.setLastName("Test");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book popularBook = new Book();
        popularBook.setIsbn("ISBN_POPULAR");
        popularBook.setTitle("Popular Book");
        popularBook.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(popularBook);

        Book unpopularBook = new Book();
        unpopularBook.setIsbn("ISBN_UNPOPULAR");
        unpopularBook.setTitle("Unpopular Book");
        unpopularBook.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(unpopularBook);

        // Create 3 loans for popular book
        for (int i = 0; i < 3; i++) {
            Loan loan = new Loan();
            loan.setBook(popularBook);
            loan.setUser(user);
            loan.setLoanDate(Instant.now().minusSeconds(86400 * (i + 1)));
            loan.setDueDate(Instant.now().plusSeconds(86400 * 14));
            loan.setStatus(LoanStatus.RETURNED);
            loan.setReturnDate(Instant.now());
            entityManager.persist(loan);
        }

        entityManager.flush();

        // When
        List<Book> topBooks = bookRepository.findTopPopularBooks(PageRequest.of(0, 10));

        // Then
        assertThat(topBooks).isNotEmpty();
        assertThat(topBooks.get(0).getTitle()).isEqualTo("Popular Book");
    }

    @Test
    @DisplayName("Should посчитать книги в категории")
    void shouldCountBooksInCategory() {
        // Given
        Category category = new Category();
        category.setName("Science");
        entityManager.persist(category);

        Book book1 = new Book();
        book1.setIsbn("SCI1");
        book1.setTitle("Science Book 1");
        book1.setStatus(BookStatus.AVAILABLE);
        book1.setCategory(category);
        entityManager.persist(book1);

        Book book2 = new Book();
        book2.setIsbn("SCI2");
        book2.setTitle("Science Book 2");
        book2.setStatus(BookStatus.AVAILABLE);
        book2.setCategory(category);
        entityManager.persist(book2);

        entityManager.flush();

        // When
        long count = bookRepository.countByCategoryId(category.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should использовать projection")
    void shouldUseProjection() {
        // Given
        Book book = new Book();
        book.setIsbn("PROJ_ISBN");
        book.setTitle("Projection Test");
        book.setStatus(BookStatus.AVAILABLE);
        book.setPublicationYear(2023);
        entityManager.persistAndFlush(book);

        // When
        List<BookRepository.BookSummary> summaries =
                bookRepository.findBookSummariesByStatus(BookStatus.AVAILABLE);

        // Then
        assertThat(summaries).isNotEmpty();
        BookRepository.BookSummary summary = summaries.get(0);
        assertThat(summary.getTitle()).isEqualTo("Projection Test");
        assertThat(summary.getIsbn()).isEqualTo("PROJ_ISBN");
    }
}
