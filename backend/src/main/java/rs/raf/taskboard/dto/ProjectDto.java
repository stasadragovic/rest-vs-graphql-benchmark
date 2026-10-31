package rs.raf.taskboard.dto;

import rs.raf.taskboard.model.Project;

import java.time.Instant;

public record ProjectDto(Long id, String name, String description, Long ownerId, Instant createdAt) {

    public static ProjectDto from(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getCreatedAt());
    }
}
