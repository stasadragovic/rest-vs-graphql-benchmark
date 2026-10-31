package rs.raf.taskboard.controller.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.graphql.data.method.annotation.BatchMapping;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

// Optimizovana varijanta: polje se razrešava jednim upitom za sve roditelje na istom nivou upita
@Controller
@ConditionalOnProperty(name = "taskboard.graphql.batching", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class BatchGraphQlResolver {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final CommentService commentService;
    private final UserService userService;

    @BatchMapping(typeName = "Project")
    public List<UserDto> owner(List<ProjectDto> projects) {
        Map<Long, UserDto> users = userService.findByIds(projects.stream().map(ProjectDto::ownerId).distinct().toList());
        return projects.stream().map(p -> users.get(p.ownerId())).toList();
    }

    @BatchMapping(typeName = "Project")
    public List<List<TaskDto>> tasks(List<ProjectDto> projects) {
        Map<Long, List<TaskDto>> tasks = taskService.findByProjects(projects.stream().map(ProjectDto::id).toList());
        return projects.stream().map(p -> tasks.getOrDefault(p.id(), List.of())).toList();
    }

    @BatchMapping(typeName = "Task")
    public List<ProjectDto> project(List<TaskDto> tasks) {
        Map<Long, ProjectDto> projects = projectService.findByIds(tasks.stream().map(TaskDto::projectId).distinct().toList());
        return tasks.stream().map(t -> projects.get(t.projectId())).toList();
    }

    // Map umesto List jer lista ne sme da sadrži null; zadatak bez zaduženog korisnika nema ključ u mapi
    @BatchMapping(typeName = "Task")
    public Map<TaskDto, UserDto> assignee(List<TaskDto> tasks) {
        Map<Long, UserDto> users = userService.findByIds(
                tasks.stream().map(TaskDto::assigneeId).filter(Objects::nonNull).distinct().toList());
        return tasks.stream()
                .filter(t -> t.assigneeId() != null)
                .collect(Collectors.toMap(Function.identity(), t -> users.get(t.assigneeId())));
    }

    @BatchMapping(typeName = "Task")
    public List<List<CommentDto>> comments(List<TaskDto> tasks) {
        Map<Long, List<CommentDto>> comments = commentService.findByTasks(tasks.stream().map(TaskDto::id).toList());
        return tasks.stream().map(t -> comments.getOrDefault(t.id(), List.of())).toList();
    }

    @BatchMapping(typeName = "Comment")
    public List<TaskDto> task(List<CommentDto> comments) {
        Map<Long, TaskDto> tasks = taskService.findByIds(comments.stream().map(CommentDto::taskId).distinct().toList());
        return comments.stream().map(c -> tasks.get(c.taskId())).toList();
    }

    @BatchMapping(typeName = "Comment")
    public List<UserDto> author(List<CommentDto> comments) {
        Map<Long, UserDto> users = userService.findByIds(comments.stream().map(CommentDto::authorId).distinct().toList());
        return comments.stream().map(c -> users.get(c.authorId())).toList();
    }

    @BatchMapping(typeName = "User")
    public List<List<TaskDto>> assignedTasks(List<UserDto> users) {
        Map<Long, List<TaskDto>> tasks = taskService.findByAssignees(users.stream().map(UserDto::id).toList());
        return users.stream().map(u -> tasks.getOrDefault(u.id(), List.of())).toList();
    }
}
