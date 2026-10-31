package rs.raf.taskboard.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import rs.raf.taskboard.dto.CreateTaskRequest;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.dto.UpdateTaskRequest;
import rs.raf.taskboard.exception.NotFoundException;
import rs.raf.taskboard.model.Project;
import rs.raf.taskboard.model.Task;
import rs.raf.taskboard.model.TaskStatus;
import rs.raf.taskboard.model.User;
import rs.raf.taskboard.repository.ProjectRepository;
import rs.raf.taskboard.repository.TaskRepository;
import rs.raf.taskboard.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Optional<TaskDto> findById(Long id) {
        return taskRepository.findById(id).map(TaskDto::from);
    }

    public Map<Long, TaskDto> findByIds(Collection<Long> ids) {
        return taskRepository.findAllById(ids).stream()
                .map(TaskDto::from)
                .collect(Collectors.toMap(TaskDto::id, Function.identity()));
    }

    public List<TaskDto> findByProject(Long projectId) {
        return taskRepository.findByProjectIdOrderByIdAsc(projectId).stream()
                .map(TaskDto::from)
                .toList();
    }

    public Map<Long, List<TaskDto>> findByProjects(Collection<Long> projectIds) {
        return taskRepository.findByProjectIdInOrderByIdAsc(projectIds).stream()
                .map(TaskDto::from)
                .collect(Collectors.groupingBy(TaskDto::projectId));
    }

    public List<TaskDto> findByAssignee(Long assigneeId) {
        return taskRepository.findByAssigneeIdOrderByIdAsc(assigneeId).stream()
                .map(TaskDto::from)
                .toList();
    }

    public Map<Long, List<TaskDto>> findByAssignees(Collection<Long> assigneeIds) {
        return taskRepository.findByAssigneeIdInOrderByIdAsc(assigneeIds).stream()
                .map(TaskDto::from)
                .collect(Collectors.groupingBy(TaskDto::assigneeId));
    }

    @Transactional
    public TaskDto create(Long projectId, @Valid CreateTaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project", projectId));

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        task.setAssignee(findAssignee(request.assigneeId()));
        task.setDueDate(request.dueDate());
        return TaskDto.from(taskRepository.save(task));
    }

    @Transactional
    public TaskDto update(Long id, @Valid UpdateTaskRequest request) {
        Task task = getTask(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setAssignee(findAssignee(request.assigneeId()));
        task.setDueDate(request.dueDate());
        // flush odmah, da @UpdateTimestamp postavi updatedAt pre pravljenja DTO-a
        return TaskDto.from(taskRepository.saveAndFlush(task));
    }

    @Transactional
    public TaskDto updateStatus(Long id, @NotNull TaskStatus status) {
        Task task = getTask(id);
        task.setStatus(status);
        return TaskDto.from(taskRepository.saveAndFlush(task));
    }

    @Transactional
    public void delete(Long id) {
        taskRepository.delete(getTask(id));
    }

    private Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    }

    private User findAssignee(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId).orElseThrow(() -> new NotFoundException("User", assigneeId));
    }
}