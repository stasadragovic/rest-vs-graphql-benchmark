package rs.raf.taskboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.taskboard.dto.TaskDto;
import rs.raf.taskboard.repository.TaskRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

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
}
