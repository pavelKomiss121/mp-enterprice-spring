/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.library.config.TestJpaAuditConfig;
import ru.mentee.library.domain.model.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestJpaAuditConfig.class)
class LoanRepositoryTest {

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

    @Autowired private LoanRepository loanRepository;

    @Autowired private TestEntityManager entityManager;

    @Test
    @DisplayName("Should найти активные выдачи пользователя")
    void shouldFindActiveLoansForUser() {
        // Given
        User user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("TEST_ISBN");
        book.setTitle("Test Book");
        book.setStatus(BookStatus.LOANED);
        entityManager.persist(book);

        Loan activeLoan = new Loan();
        activeLoan.setBook(book);
        activeLoan.setUser(user);
        activeLoan.setLoanDate(Instant.now());
        activeLoan.setDueDate(Instant.now().plus(14, ChronoUnit.DAYS));
        activeLoan.setStatus(LoanStatus.ACTIVE);
        entityManager.persistAndFlush(activeLoan);

        // When
        List<Loan> loans = loanRepository.findByUserIdAndStatus(user.getId(), LoanStatus.ACTIVE);

        // Then
        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).getBook().getTitle()).isEqualTo("Test Book");
    }

    @Test
    @DisplayName("Should найти просроченные выдачи")
    void shouldFindOverdueLoans() {
        // Given
        User user = new User();
        user.setEmail("overdue@example.com");
        user.setFirstName("Overdue");
        user.setLastName("Reader");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("OVERDUE_ISBN");
        book.setTitle("Overdue Book");
        book.setStatus(BookStatus.LOANED);
        entityManager.persist(book);

        Loan overdueLoan = new Loan();
        overdueLoan.setBook(book);
        overdueLoan.setUser(user);
        overdueLoan.setLoanDate(Instant.now().minus(30, ChronoUnit.DAYS));
        overdueLoan.setDueDate(Instant.now().minus(2, ChronoUnit.DAYS)); // Просрочена на 2 дня
        overdueLoan.setStatus(LoanStatus.ACTIVE);
        entityManager.persistAndFlush(overdueLoan);

        // When
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(Instant.now());

        // Then
        assertThat(overdueLoans).hasSize(1);
        assertThat(overdueLoans.get(0).getBook().getTitle()).isEqualTo("Overdue Book");
    }

    @Test
    @DisplayName("Should посчитать выдачи за период")
    void shouldCountLoansByPeriod() {
        // Given
        User user = new User();
        user.setEmail("stat@example.com");
        user.setFirstName("Stat");
        user.setLastName("User");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("STAT_ISBN");
        book.setTitle("Statistics Book");
        book.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book);

        Instant startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant endDate = Instant.now();

        // Create loans within period
        for (int i = 0; i < 5; i++) {
            Loan loan = new Loan();
            loan.setBook(book);
            loan.setUser(user);
            loan.setLoanDate(Instant.now().minus(i * 5, ChronoUnit.DAYS));
            loan.setDueDate(Instant.now().plus(14, ChronoUnit.DAYS));
            loan.setStatus(LoanStatus.RETURNED);
            loan.setReturnDate(Instant.now());
            entityManager.persist(loan);
        }

        entityManager.flush();

        // When
        long count = loanRepository.countLoansByPeriod(startDate, endDate);

        // Then
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("Should получить статистику выдач")
    void shouldGetLoanStatistics() {
        // Given
        User user = new User();
        user.setEmail("stats@example.com");
        user.setFirstName("Stats");
        user.setLastName("User");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("STATS_ISBN");
        book.setTitle("Stats Book");
        book.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book);

        Instant startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant endDate = Instant.now();

        // Create various status loans
        Loan activeLoan = new Loan();
        activeLoan.setBook(book);
        activeLoan.setUser(user);
        activeLoan.setLoanDate(Instant.now().minus(5, ChronoUnit.DAYS));
        activeLoan.setDueDate(Instant.now().plus(9, ChronoUnit.DAYS));
        activeLoan.setStatus(LoanStatus.ACTIVE);
        entityManager.persist(activeLoan);

        Loan returnedLoan = new Loan();
        returnedLoan.setBook(book);
        returnedLoan.setUser(user);
        returnedLoan.setLoanDate(Instant.now().minus(20, ChronoUnit.DAYS));
        returnedLoan.setDueDate(Instant.now().minus(6, ChronoUnit.DAYS));
        returnedLoan.setStatus(LoanStatus.RETURNED);
        returnedLoan.setReturnDate(Instant.now().minus(7, ChronoUnit.DAYS));
        entityManager.persist(returnedLoan);

        entityManager.flush();

        // When
        LoanRepository.LoanStatistics stats = loanRepository.getLoanStatistics(startDate, endDate);

        // Then
        assertThat(stats.getTotalLoans()).isEqualTo(2);
        assertThat(stats.getActiveLoans()).isEqualTo(1);
        assertThat(stats.getReturnedLoans()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should найти историю выдач книги")
    void shouldFindLoanHistoryForBook() {
        // Given
        User user = new User();
        user.setEmail("history@example.com");
        user.setFirstName("History");
        user.setLastName("User");
        user.setRegisteredAt(Instant.now());
        entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("HISTORY_ISBN");
        book.setTitle("History Book");
        book.setStatus(BookStatus.AVAILABLE);
        entityManager.persist(book);

        // Create multiple loans for the same book
        for (int i = 0; i < 3; i++) {
            Loan loan = new Loan();
            loan.setBook(book);
            loan.setUser(user);
            loan.setLoanDate(Instant.now().minus((i + 1) * 20, ChronoUnit.DAYS));
            loan.setDueDate(Instant.now().minus((i + 1) * 20 - 14, ChronoUnit.DAYS));
            loan.setStatus(LoanStatus.RETURNED);
            loan.setReturnDate(Instant.now().minus((i + 1) * 20 - 10, ChronoUnit.DAYS));
            entityManager.persist(loan);
        }

        entityManager.flush();

        // When
        List<Loan> history = loanRepository.findByBookIdOrderByLoanDateDesc(book.getId());

        // Then
        assertThat(history).hasSize(3);
        // Проверяем, что отсортировано по дате DESC
        assertThat(history.get(0).getLoanDate()).isAfter(history.get(1).getLoanDate());
    }
}
