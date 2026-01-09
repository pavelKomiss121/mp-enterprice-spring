/* @MENTEE_POWER (C)2026 */
package ru.mentee.todo.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.todo.api.dto.CreateTaskRequest;
import ru.mentee.todo.api.dto.TaskDto;
import ru.mentee.todo.domain.model.Task;
import ru.mentee.todo.domain.repository.TaskRepository;
import ru.mentee.todo.service.TaskService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @PostConstruct
    public void init() {
        log.info("bean initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("bean destroyed");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        log.info("getAllTasks");
        List<Task> tasks = taskRepository.findAll();
        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskDto taskDto = toDto(task);
            taskDtos.add(taskDto);
        }
        return taskDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        log.info("Getting task by ID: {}", id);
        Task task =
                taskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return toDto(task);
    }

    @Override
    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        log.info("createTask");
        Task task = new Task(null, request.getTitle(), false, LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long id, CreateTaskRequest request) {
        log.info("Updating task with ID: {}", id);
        Task task =
                taskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(request.getTitle());

        Task updatedTask = taskRepository.save(task);
        return toDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        log.info("delete task {}", id);
        taskRepository.deleteById(id);
    }

    private TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
