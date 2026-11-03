package rs.raf.taskboard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        @Min(1) @Max(3) Short priority,
        Long assigneeId,
        LocalDate dueDate) {
}