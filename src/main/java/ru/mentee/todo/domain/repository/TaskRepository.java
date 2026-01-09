/* @MENTEE_POWER (C)2026 */
package ru.mentee.todo.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import ru.mentee.todo.domain.model.Task;

@Repository
public class TaskRepository {

    // Хранилище в памяти (пока без БД)
    private final List<Task> tasks = new ArrayList<>();
    private Long nextId = 1L;

    public List<Task> findAll() {
        return tasks;
    }

    public Optional<Task> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(nextId++);
            tasks.add(task);
            return task;
        } else {
            Task existingTask =
                    findById(task.getId())
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Task not found with id: " + task.getId()));
            existingTask.setTitle(task.getTitle());
            existingTask.setCompleted(task.getCompleted());
            return existingTask;
        }
    }

    public void deleteById(Long id) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (!removed) {
            throw new RuntimeException("Task not found");
        }
    }

    public boolean existsById(Long id) {
        return id != null && tasks.stream().anyMatch(task -> task.getId().equals(id));
    }
}
