package rs.raf.taskboard.dto;

import rs.raf.taskboard.model.Comment;

import java.time.Instant;

public record CommentDto(Long id, Long taskId, Long authorId, String text, Instant createdAt) {

    public static CommentDto from(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getTask().getId(),
                comment.getAuthor().getId(),
                comment.getText(),
                comment.getCreatedAt());
    }
}
