/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager.api.controller;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mentee.taskmanager.api.generated.controller.TasksApi;
import ru.mentee.taskmanager.api.generated.dto.CreateTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.PatchTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.Task;
import ru.mentee.taskmanager.api.generated.dto.TaskListResponse;
import ru.mentee.taskmanager.api.generated.dto.UpdateTaskRequest;
import ru.mentee.taskmanager.domain.model.Task.TaskPriority;
import ru.mentee.taskmanager.domain.model.Task.TaskStatus;
import ru.mentee.taskmanager.service.TaskService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController implements TasksApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskListResponse> getTasks(
            String status,
            String assignee,
            String priority,
            String sort,
            Integer page,
            Integer size) {
        log.info(
                "Getting tasks: status={}, assignee={}, priority={}, sort={}, page={}, size={}",
                status,
                assignee,
                priority,
                sort,
                page,
                size);

        TaskStatus taskStatus = status != null ? TaskStatus.valueOf(status) : null;
        TaskPriority taskPriority = priority != null ? TaskPriority.valueOf(priority) : null;

        Pageable pageable = createPageable(page, size, sort);
        var taskPage = taskService.getTasks(taskStatus, assignee, taskPriority, pageable);

        TaskListResponse response = new TaskListResponse();
        response.setData(taskPage.getContent());
        response.setPagination(
                createPagination(
                        taskPage.getNumber(),
                        taskPage.getSize(),
                        taskPage.getTotalElements(),
                        taskPage.getTotalPages()));

        // HATEOAS links для пагинации
        var links = new ru.mentee.taskmanager.api.generated.dto.TaskListResponseLinks();
        links.setSelf(createLink("/api/v1/tasks?page=" + page + "&size=" + size, "GET"));
        if (taskPage.hasPrevious()) {
            links.setPrev(createLink("/api/v1/tasks?page=" + (page - 1) + "&size=" + size, "GET"));
        }
        if (taskPage.hasNext()) {
            links.setNext(createLink("/api/v1/tasks?page=" + (page + 1) + "&size=" + size, "GET"));
        }
        links.setFirst(createLink("/api/v1/tasks?page=0&size=" + size, "GET"));
        links.setLast(
                createLink(
                        "/api/v1/tasks?page=" + (taskPage.getTotalPages() - 1) + "&size=" + size,
                        "GET"));
        response.setLinks(links);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Task> getTaskById(UUID taskId) {
        log.info("Getting task by id: {}", taskId);
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @Override
    public ResponseEntity<Task> createTask(CreateTaskRequest createTaskRequest) {
        log.info("Creating task: title={}", createTaskRequest.getTitle());
        Task task = taskService.createTask(createTaskRequest);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(task.getId())
                        .toUri();
        return ResponseEntity.created(location).body(task);
    }

    @Override
    public ResponseEntity<Task> updateTask(UUID taskId, UpdateTaskRequest updateTaskRequest) {
        log.info("Updating task: id={}", taskId);
        Task task = taskService.updateTask(taskId, updateTaskRequest);
        return ResponseEntity.ok(task);
    }

    @Override
    public ResponseEntity<Task> patchTask(UUID taskId, PatchTaskRequest patchTaskRequest) {
        log.info("Patching task: id={}", taskId);
        Task task = taskService.patchTask(taskId, patchTaskRequest);
        return ResponseEntity.ok(task);
    }

    @Override
    public ResponseEntity<Void> deleteTask(UUID taskId) {
        log.info("Deleting task: id={}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageable(Integer page, Integer size, String sort) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            Sort.Direction direction = Sort.Direction.ASC;
            String property = "createdAt";

            if (sortParts.length > 0) {
                String[] firstSort = sortParts[0].split(":");
                if (firstSort.length == 2) {
                    property = firstSort[0];
                    direction =
                            "desc".equalsIgnoreCase(firstSort[1])
                                    ? Sort.Direction.DESC
                                    : Sort.Direction.ASC;
                }
            }

            return PageRequest.of(pageNum, pageSize, Sort.by(direction, property));
        }

        return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private ru.mentee.taskmanager.api.generated.dto.Pagination createPagination(
            int page, int size, long totalElements, int totalPages) {
        var pagination = new ru.mentee.taskmanager.api.generated.dto.Pagination();
        pagination.setPage(page);
        pagination.setSize(size);
        pagination.setTotalElements((int) totalElements);
        pagination.setTotalPages(totalPages);
        return pagination;
    }

    private ru.mentee.taskmanager.api.generated.dto.Link createLink(String href, String method) {
        var link = new ru.mentee.taskmanager.api.generated.dto.Link();
        link.setHref(href);
        link.setMethod(ru.mentee.taskmanager.api.generated.dto.Link.MethodEnum.fromValue(method));
        return link;
    }
}
