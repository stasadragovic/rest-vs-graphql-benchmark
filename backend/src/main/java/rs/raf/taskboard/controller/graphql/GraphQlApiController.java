package rs.raf.taskboard.controller.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rs.raf.taskboard.dto.AuthResponse;
import rs.raf.taskboard.dto.CommentDto;
import rs.raf.taskboard.dto.CreateCommentRequest;
import rs.raf.taskboard.dto.CreateTaskRequest;
import rs.raf.taskboard.dto.LoginRequest;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.dto.RegisterRequest;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.dto.UpdateCommentRequest;
import rs.raf.taskboard.dto.UpdateTaskRequest;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.exception.NotFoundException;
import rs.raf.taskboard.model.TaskStatus;
import rs.raf.taskboard.service.AuthService;
import rs.raf.taskboard.service.CommentService;
import rs.raf.taskboard.service.ProjectService;
import rs.raf.taskboard.service.TaskService;
import rs.raf.taskboard.service.UserService;

import java.util.List;

// GraphQL ima jedan URL za sve, pa se token proverava ovde, a ne u SecurityConfig
@Controller
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class GraphQlApiController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final CommentService commentService;
    private final UserService userService;
    private final AuthService authService;


    @MutationMapping
    @PreAuthorize("permitAll()")
    public AuthResponse register(@Argument RegisterRequest input) {
        return authService.register(input);
    }

    @MutationMapping
    @PreAuthorize("permitAll()")
    public AuthResponse login(@Argument String email, @Argument String password) {
        return authService.login(new LoginRequest(email, password));
    }

    @QueryMapping
    public List<ProjectDto> projects(@Argument int limit) {
        return projectService.findAll(limit);
    }

    @QueryMapping
    public ProjectDto project(@Argument Long id) {
        return projectService.findById(id).orElse(null);
    }

    @QueryMapping
    public TaskDto task(@Argument Long id) {
        return taskService.findById(id).orElse(null);
    }

    @QueryMapping
    public List<UserDto> users(@Argument List<Long> ids) {
        return userService.findAll(ids);
    }

    @QueryMapping
    public UserDto user(@Argument Long id) {
        return userService.findById(id).orElse(null);
    }

    @QueryMapping
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        Long id = Long.valueOf(jwt.getSubject());
        return userService.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    @MutationMapping
    public TaskDto createTask(@Argument Long projectId, @Argument CreateTaskRequest input) {
        return taskService.create(projectId, input);
    }

    @MutationMapping
    public TaskDto updateTask(@Argument Long id, @Argument UpdateTaskRequest input) {
        return taskService.update(id, input);
    }

    @MutationMapping
    public TaskDto updateTaskStatus(@Argument Long id, @Argument TaskStatus status) {
        return taskService.updateStatus(id, status);
    }

    @MutationMapping
    public boolean deleteTask(@Argument Long id) {
        taskService.delete(id);
        return true;
    }

    @MutationMapping
    public CommentDto createComment(@Argument Long taskId, @Argument String text, @AuthenticationPrincipal Jwt jwt) {
        return commentService.create(taskId, Long.valueOf(jwt.getSubject()), new CreateCommentRequest(text));
    }

    @MutationMapping
    public CommentDto updateComment(@Argument Long id, @Argument String text) {
        return commentService.update(id, new UpdateCommentRequest(text));
    }

    @MutationMapping
    public boolean deleteComment(@Argument Long id) {
        commentService.delete(id);
        return true;
    }
}