package rs.raf.taskboard.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rs.raf.taskboard.dto.CommentDto;
import rs.raf.taskboard.dto.CreateTaskRequest;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.dto.UpdateCommentRequest;
import rs.raf.taskboard.dto.UpdateTaskRequest;
import rs.raf.taskboard.dto.UpdateTaskStatusRequest;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.exception.NotFoundException;
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
        return projectService.findById(id).orElseThrow(() -> new NotFoundException("Project", id));
    }

    @GetMapping("/projects/{id}/tasks")
    public List<TaskDto> getProjectTasks(@PathVariable Long id) {
        return taskService.findByProject(id);
    }

    @GetMapping("/tasks/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    }

    @GetMapping("/tasks/{id}/comments")
    public List<CommentDto> getTaskComments(@PathVariable Long id) {
        return commentService.findByTask(id);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids) {
        return userService.findAll(ids);
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    @GetMapping("/users/{id}/tasks")
    public List<TaskDto> getUserTasks(@PathVariable Long id) {
        return taskService.findByAssignee(id);
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@PathVariable Long projectId, @RequestBody CreateTaskRequest request) {
        return taskService.create(projectId, request);
    }

    @PutMapping("/tasks/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request) {
        return taskService.update(id, request);
    }

    @PatchMapping("/tasks/{id}/status")
    public TaskDto updateTaskStatus(@PathVariable Long id, @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateStatus(id, request.status());
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.delete(id);
    }

    @PutMapping("/comments/{id}")
    public CommentDto updateComment(@PathVariable Long id, @RequestBody UpdateCommentRequest request) {
        return commentService.update(id, request);
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.delete(id);
    }
}
