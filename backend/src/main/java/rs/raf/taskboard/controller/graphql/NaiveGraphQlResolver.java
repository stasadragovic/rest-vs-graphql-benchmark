package rs.raf.taskboard.controller.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import rs.raf.taskboard.dto.CommentDto;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.service.CommentService;
import rs.raf.taskboard.service.ProjectService;
import rs.raf.taskboard.service.TaskService;
import rs.raf.taskboard.service.UserService;

import java.util.List;

// Naivna varijanta: svako ugnežđeno polje se razrešava posebnim upitom za svaki objekat (N+1)
@Controller
@ConditionalOnProperty(name = "taskboard.graphql.batching", havingValue = "false")
@RequiredArgsConstructor
public class NaiveGraphQlResolver {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final CommentService commentService;
    private final UserService userService;

    @SchemaMapping(typeName = "Project")
    public UserDto owner(ProjectDto project) {
        return userService.findById(project.ownerId()).orElseThrow();
    }

    @SchemaMapping(typeName = "Project")
    public List<TaskDto> tasks(ProjectDto project) {
        return taskService.findByProject(project.id());
    }

    @SchemaMapping(typeName = "Task")
    public ProjectDto project(TaskDto task) {
        return projectService.findById(task.projectId()).orElseThrow();
    }

    @SchemaMapping(typeName = "Task")
    public UserDto assignee(TaskDto task) {
        return task.assigneeId() != null ? userService.findById(task.assigneeId()).orElseThrow() : null;
    }

    @SchemaMapping(typeName = "Task")
    public List<CommentDto> comments(TaskDto task) {
        return commentService.findByTask(task.id());
    }

    @SchemaMapping(typeName = "Comment")
    public TaskDto task(CommentDto comment) {
        return taskService.findById(comment.taskId()).orElseThrow();
    }

    @SchemaMapping(typeName = "Comment")
    public UserDto author(CommentDto comment) {
        return userService.findById(comment.authorId()).orElseThrow();
    }
}
