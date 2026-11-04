package rs.raf.taskboard.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(@NotBlank String text) {
}