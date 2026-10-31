package rs.raf.taskboard.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rs.raf.taskboard.dto.CommentDto;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.service.CommentService;
import rs.raf.taskboard.service.ProjectService;
import rs.raf.taskboard.service.TaskService;
import rs.raf.taskboard.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestApiController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/projects")
    public List<ProjectDto> getProjects(@RequestParam(defaultValue = "20") int limit) {
        return projectService.findAll(limit);
    }

    @GetMapping("/projects/{id}")
    public ProjectDto getProject(@PathVariable Long id) {
        return projectService.findById(id).orElseThrow(this::notFound);
    }

    @GetMapping("/projects/{id}/tasks")
    public List<TaskDto> getProjectTasks(@PathVariable Long id) {
        return taskService.findByProject(id);
    }

    @GetMapping("/tasks/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.findById(id).orElseThrow(this::notFound);
    }

    @GetMapping("/tasks/{id}/comments")
    public List<CommentDto> getTaskComments(@PathVariable Long id) {
        return commentService.findByTask(id);
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.findById(id).orElseThrow(this::notFound);
    }

    @GetMapping("/users/{id}/tasks")
    public List<TaskDto> getUserTasks(@PathVariable Long id) {
        return taskService.findByAssignee(id);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
