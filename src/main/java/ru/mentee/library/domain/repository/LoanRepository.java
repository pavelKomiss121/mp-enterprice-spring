/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.library.domain.model.Loan;
import ru.mentee.library.domain.model.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // ============================================
    // Derived Query Methods
    // ============================================

    // Активные выдачи пользователя
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    // История выдач книги
    List<Loan> findByBookIdOrderByLoanDateDesc(Long bookId);

    // Просроченные выдачи
    List<Loan> findByStatus(LoanStatus status);

    // Выдачи в диапазоне дат
    List<Loan> findByLoanDateBetween(Instant startDate, Instant endDate);

    // ============================================
    // @Query Methods - Сложные запросы
    // ============================================

    // Активные выдачи пользователя с деталями книги
    @Query(
            """
        SELECT l FROM Loan l
        JOIN FETCH l.book
        JOIN FETCH l.user
        WHERE l.user.id = :userId
        AND l.status = 'ACTIVE'
        ORDER BY l.loanDate DESC
        """)
    List<Loan> findActiveLoansForUser(@Param("userId") Long userId);

    // История выдач книги с деталями пользователя
    @Query(
            """
        SELECT l FROM Loan l
        JOIN FETCH l.user
        WHERE l.book.id = :bookId
        ORDER BY l.loanDate DESC
        """)
    List<Loan> findLoanHistoryForBook(@Param("bookId") Long bookId);

    // Просроченные выдачи (статус ACTIVE и дата возврата прошла)
    @Query(
            """
        SELECT l FROM Loan l
        WHERE l.status = 'ACTIVE'
        AND l.dueDate < :currentDate
        """)
    List<Loan> findOverdueLoans(@Param("currentDate") Instant currentDate);

    // Статистика по периодам - количество выдач
    @Query(
            """
        SELECT COUNT(l) FROM Loan l
        WHERE l.loanDate BETWEEN :startDate AND :endDate
        """)
    long countLoansByPeriod(
            @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    // Статистика по периодам - количество возвратов
    @Query(
            """
        SELECT COUNT(l) FROM Loan l
        WHERE l.returnDate BETWEEN :startDate AND :endDate
        AND l.status = 'RETURNED'
        """)
    long countReturnsByPeriod(
            @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    // Топ активных читателей (по количеству выдач)
    @Query(
            """
        SELECT l.user.id as userId,
               l.user.firstName as userFirstName,
               l.user.lastName as userLastName,
               COUNT(l) as loanCount
        FROM Loan l
        GROUP BY l.user.id, l.user.firstName, l.user.lastName
        ORDER BY COUNT(l) DESC
        """)
    List<TopBorrower> findTopBorrowers();

    // ============================================
    // Projections
    // ============================================

    // Projection для активных выдач
    interface ActiveLoanInfo {
        Long getId();

        Instant getLoanDate();

        Instant getDueDate();

        interface BookInfo {
            String getTitle();

            String getIsbn();
        }

        BookInfo getBook();

        interface UserInfo {
            String getFirstName();

            String getLastName();

            String getEmail();
        }

        UserInfo getUser();
    }

    @Query(
            """
        SELECT l FROM Loan l
        WHERE l.user.id = :userId
        AND l.status = 'ACTIVE'
        """)
    List<ActiveLoanInfo> findActiveLoanInfoForUser(@Param("userId") Long userId);

    // Projection для топ заёмщиков
    interface TopBorrower {
        Long getUserId();

        String getUserFirstName();

        String getUserLastName();

        Long getLoanCount();
    }

    // Статистика по периодам - projection
    interface LoanStatistics {
        Long getTotalLoans();

        Long getActiveLoans();

        Long getReturnedLoans();

        Long getOverdueLoans();
    }

    @Query(
            """
        SELECT
            COUNT(l) as totalLoans,
            SUM(CASE WHEN l.status = 'ACTIVE' THEN 1 ELSE 0 END) as activeLoans,
            SUM(CASE WHEN l.status = 'RETURNED' THEN 1 ELSE 0 END) as returnedLoans,
            SUM(CASE WHEN l.status = 'OVERDUE' THEN 1 ELSE 0 END) as overdueLoans
        FROM Loan l
        WHERE l.loanDate BETWEEN :startDate AND :endDate
        """)
    LoanStatistics getLoanStatistics(
            @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
