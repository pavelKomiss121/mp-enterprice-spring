/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager.domain.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.taskmanager.domain.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query(
            "SELECT t FROM Task t WHERE "
                    + "(:status IS NULL OR t.status = :status) AND "
                    + "(:assignee IS NULL OR t.assignee = :assignee) AND "
                    + "(:priority IS NULL OR t.priority = :priority)")
    Page<Task> findByFilters(
            @Param("status") Task.TaskStatus status,
            @Param("assignee") String assignee,
            @Param("priority") Task.TaskPriority priority,
            Pageable pageable);
}
