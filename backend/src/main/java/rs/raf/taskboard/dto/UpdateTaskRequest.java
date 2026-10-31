package rs.raf.taskboard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import rs.raf.taskboard.model.TaskStatus;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        @NotNull TaskStatus status,
        @NotNull @Min(1) @Max(3) Short priority,
        Long assigneeId,
        LocalDate dueDate) {
}