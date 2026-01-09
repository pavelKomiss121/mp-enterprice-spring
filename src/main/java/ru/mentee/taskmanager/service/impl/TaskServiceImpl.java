/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.taskmanager.api.dto.mapper.TaskMapper;
import ru.mentee.taskmanager.api.exception.ResourceNotFoundException;
import ru.mentee.taskmanager.api.generated.dto.CreateTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.PatchTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.UpdateTaskRequest;
import ru.mentee.taskmanager.domain.model.Task.TaskPriority;
import ru.mentee.taskmanager.domain.model.Task.TaskStatus;
import ru.mentee.taskmanager.domain.repository.TaskRepository;
import ru.mentee.taskmanager.service.TaskService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ru.mentee.taskmanager.api.generated.dto.Task> getTasks(
            TaskStatus status, String assignee, TaskPriority priority, Pageable pageable) {
        log.info(
                "Getting tasks: status={}, assignee={}, priority={}, page={}, size={}",
                status,
                assignee,
                priority,
                pageable.getPageNumber(),
                pageable.getPageSize());

        Page<ru.mentee.taskmanager.domain.model.Task> tasks =
                taskRepository.findByFilters(status, assignee, priority, pageable);
        return tasks.map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ru.mentee.taskmanager.api.generated.dto.Task getTaskById(UUID taskId) {
        log.info("Getting task by id: {}", taskId);
        ru.mentee.taskmanager.domain.model.Task task =
                taskRepository
                        .findById(taskId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task not found with id: " + taskId));
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public ru.mentee.taskmanager.api.generated.dto.Task createTask(CreateTaskRequest request) {
        log.info("Creating task: title={}, priority={}", request.getTitle(), request.getPriority());
        ru.mentee.taskmanager.domain.model.Task task = taskMapper.toDomain(request);
        ru.mentee.taskmanager.domain.model.Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public ru.mentee.taskmanager.api.generated.dto.Task updateTask(
            UUID taskId, UpdateTaskRequest request) {
        log.info("Updating task: id={}", taskId);
        ru.mentee.taskmanager.domain.model.Task task =
                taskRepository
                        .findById(taskId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task not found with id: " + taskId));
        taskMapper.updateDomain(task, request);
        ru.mentee.taskmanager.domain.model.Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public ru.mentee.taskmanager.api.generated.dto.Task patchTask(
            UUID taskId, PatchTaskRequest request) {
        log.info("Patching task: id={}", taskId);
        ru.mentee.taskmanager.domain.model.Task task =
                taskRepository
                        .findById(taskId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task not found with id: " + taskId));
        taskMapper.patchDomain(task, request);
        ru.mentee.taskmanager.domain.model.Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId) {
        log.info("Deleting task: id={}", taskId);
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
