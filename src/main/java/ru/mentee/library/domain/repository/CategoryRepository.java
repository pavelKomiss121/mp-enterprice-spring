/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.library.domain.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Поиск по имени
    Optional<Category> findByName(String name);

    // Проверка существования категории
    boolean existsByName(String name);
}
