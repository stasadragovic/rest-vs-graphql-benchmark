package rs.raf.taskboard.controller.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.service.ProjectService;
import rs.raf.taskboard.service.TaskService;
import rs.raf.taskboard.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphQlApiController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final UserService userService;

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
    public UserDto user(@Argument Long id) {
        return userService.findById(id).orElse(null);
    }
}
