/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager.api.dto.mapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;
import ru.mentee.taskmanager.api.generated.dto.CreateTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.Link;
import ru.mentee.taskmanager.api.generated.dto.PatchTaskRequest;
import ru.mentee.taskmanager.api.generated.dto.TaskLinks;
import ru.mentee.taskmanager.api.generated.dto.UpdateTaskRequest;

@Component
public class TaskMapper {

    public ru.mentee.taskmanager.api.generated.dto.Task toDto(
            ru.mentee.taskmanager.domain.model.Task domainTask) {
        ru.mentee.taskmanager.api.generated.dto.Task task =
                new ru.mentee.taskmanager.api.generated.dto.Task();
        task.setId(domainTask.getId());
        task.setTitle(domainTask.getTitle());
        task.setDescription(domainTask.getDescription());
        if (domainTask.getStatus() != null) {
            task.setStatus(
                    ru.mentee.taskmanager.api.generated.dto.Task.StatusEnum.fromValue(
                            domainTask.getStatus().name()));
        }
        if (domainTask.getPriority() != null) {
            task.setPriority(
                    ru.mentee.taskmanager.api.generated.dto.Task.PriorityEnum.fromValue(
                            domainTask.getPriority().name()));
        }
        task.setAssignee(domainTask.getAssignee());
        if (domainTask.getDueDate() != null) {
            task.setDueDate(OffsetDateTime.of(domainTask.getDueDate(), ZoneOffset.UTC));
        }
        task.setTags(domainTask.getTags());
        if (domainTask.getCreatedAt() != null) {
            task.setCreatedAt(OffsetDateTime.of(domainTask.getCreatedAt(), ZoneOffset.UTC));
        }
        if (domainTask.getUpdatedAt() != null) {
            task.setUpdatedAt(OffsetDateTime.of(domainTask.getUpdatedAt(), ZoneOffset.UTC));
        }

        // HATEOAS links
        TaskLinks links = new TaskLinks();
        Link selfLink = new Link();
        selfLink.setHref("/api/v1/tasks/" + domainTask.getId());
        selfLink.setMethod(Link.MethodEnum.GET);
        links.setSelf(selfLink);

        Link commentsLink = new Link();
        commentsLink.setHref("/api/v1/tasks/" + domainTask.getId() + "/comments");
        commentsLink.setMethod(Link.MethodEnum.GET);
        links.setComments(commentsLink);

        Link updateLink = new Link();
        updateLink.setHref("/api/v1/tasks/" + domainTask.getId());
        updateLink.setMethod(Link.MethodEnum.PUT);
        links.setUpdate(updateLink);

        Link deleteLink = new Link();
        deleteLink.setHref("/api/v1/tasks/" + domainTask.getId());
        deleteLink.setMethod(Link.MethodEnum.DELETE);
        links.setDelete(deleteLink);

        task.setLinks(links);
        return task;
    }

    public ru.mentee.taskmanager.domain.model.Task toDomain(CreateTaskRequest request) {
        ru.mentee.taskmanager.domain.model.Task task =
                new ru.mentee.taskmanager.domain.model.Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(
                    ru.mentee.taskmanager.domain.model.Task.TaskPriority.valueOf(
                            request.getPriority().getValue()));
        }
        task.setAssignee(request.getAssignee());
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate().toLocalDateTime());
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }
        return task;
    }

    public void updateDomain(
            ru.mentee.taskmanager.domain.model.Task task, UpdateTaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(
                    ru.mentee.taskmanager.domain.model.Task.TaskStatus.valueOf(
                            request.getStatus().getValue()));
        }
        if (request.getPriority() != null) {
            task.setPriority(
                    ru.mentee.taskmanager.domain.model.Task.TaskPriority.valueOf(
                            request.getPriority().getValue()));
        }
        task.setAssignee(request.getAssignee());
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate().toLocalDateTime());
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }
    }

    public void patchDomain(
            ru.mentee.taskmanager.domain.model.Task task, PatchTaskRequest request) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(
                    ru.mentee.taskmanager.domain.model.Task.TaskStatus.valueOf(
                            request.getStatus().getValue()));
        }
        if (request.getPriority() != null) {
            task.setPriority(
                    ru.mentee.taskmanager.domain.model.Task.TaskPriority.valueOf(
                            request.getPriority().getValue()));
        }
        if (request.getAssignee() != null) {
            task.setAssignee(request.getAssignee());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate().toLocalDateTime());
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }
    }
}
