package rs.raf.taskboard.dto;

import rs.raf.taskboard.model.TaskStatus;

public record UpdateTaskStatusRequest(TaskStatus status) {
}