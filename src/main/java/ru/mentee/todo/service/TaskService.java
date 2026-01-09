/* @MENTEE_POWER (C)2026 */
package ru.mentee.todo.service;

import java.util.List;
import ru.mentee.todo.api.dto.CreateTaskRequest;
import ru.mentee.todo.api.dto.TaskDto;

public interface TaskService {
    List<TaskDto> getAllTasks();

    TaskDto getTaskById(Long id);

    TaskDto createTask(CreateTaskRequest request);

    TaskDto updateTask(Long id, CreateTaskRequest request);

    void deleteTask(Long id);
}
