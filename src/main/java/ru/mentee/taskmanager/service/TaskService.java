/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.taskmanager.api.generated.dto.CreateTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.PatchTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.UpdateTaskRequest;
import ru.mentee.taskmanager.domain.model.Task.TaskPriority;
import ru.mentee.taskmanager.domain.model.Task.TaskStatus;

public interface TaskService {

    Page<ru.mentee.taskmanager.api.generated.dto.Task> getTasks(
            TaskStatus status, String assignee, TaskPriority priority, Pageable pageable);

    ru.mentee.taskmanager.api.generated.dto.Task getTaskById(UUID taskId);

    ru.mentee.taskmanager.api.generated.dto.Task createTask(CreateTaskRequest request);

    ru.mentee.taskmanager.api.generated.dto.Task updateTask(UUID taskId, UpdateTaskRequest request);

    ru.mentee.taskmanager.api.generated.dto.Task patchTask(UUID taskId, PatchTaskRequest request);

    void deleteTask(UUID taskId);
}
