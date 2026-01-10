/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.library.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск по email
    Optional<User> findByEmail(String email);

    // Проверка существования по email
    boolean existsByEmail(String email);

    // Поиск по имени и фамилии
    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    // Поиск пользователей с активными выдачами
    @Query(
            """
        SELECT DISTINCT u FROM User u
        JOIN Loan l ON l.user = u
        WHERE l.status = 'ACTIVE'
        """)
    List<User> findUsersWithActiveLoans();

    // Пользователи с просроченными выдачами
    @Query(
            """
        SELECT DISTINCT u FROM User u
        JOIN Loan l ON l.user = u
        WHERE l.status = 'OVERDUE'
        """)
    List<User> findUsersWithOverdueLoans();

    // Количество активных выдач пользователя
    @Query(
            """
        SELECT COUNT(l) FROM Loan l
        WHERE l.user.id = :userId
        AND l.status = 'ACTIVE'
        """)
    long countActiveLoansForUser(@Param("userId") Long userId);
}
