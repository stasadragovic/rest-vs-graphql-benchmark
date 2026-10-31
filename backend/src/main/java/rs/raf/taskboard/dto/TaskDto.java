package rs.raf.taskboard.dto;

import rs.raf.taskboard.model.Task;
import rs.raf.taskboard.model.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskDto(
        Long id,
        Long projectId,
        String title,
        String description,
        TaskStatus status,
        Short priority,
        Long assigneeId,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt) {

    public static TaskDto from(Task task) {
        return new TaskDto(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
