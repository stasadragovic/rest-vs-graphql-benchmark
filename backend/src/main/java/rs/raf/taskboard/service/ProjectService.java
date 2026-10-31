package rs.raf.taskboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.repository.ProjectRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectDto> findAll(int limit) {
        return projectRepository.findByOrderByIdAsc(Limit.of(limit)).stream()
                .map(ProjectDto::from)
                .toList();
    }

    public Optional<ProjectDto> findById(Long id) {
        return projectRepository.findById(id).map(ProjectDto::from);
    }

    public Map<Long, ProjectDto> findByIds(Collection<Long> ids) {
        return projectRepository.findAllById(ids).stream()
                .map(ProjectDto::from)
                .collect(Collectors.toMap(ProjectDto::id, Function.identity()));
    }
}
