package rs.raf.taskboard.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import rs.raf.taskboard.dto.ProjectDto;
import rs.raf.taskboard.dto.ProjectRequest;
import rs.raf.taskboard.exception.NotFoundException;
import rs.raf.taskboard.model.Project;
import rs.raf.taskboard.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

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

    public List<ProjectDto> findByOwner(Long ownerId) {
        return projectRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(ProjectDto::from)
                .toList();
    }

    public Map<Long, List<ProjectDto>> findByOwners(Collection<Long> ownerIds) {
        return projectRepository.findByOwnerIdInOrderByIdAsc(ownerIds).stream()
                .map(ProjectDto::from)
                .collect(Collectors.groupingBy(ProjectDto::ownerId));
    }

    @Transactional
    public ProjectDto create(Long ownerId, @Valid ProjectRequest request) {
        Project project = new Project();
        project.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User", ownerId)));
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectDto.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto update(Long id, @Valid ProjectRequest request) {
        Project project = getProject(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectDto.from(project);
    }

    // Zadatke i komentare projekta briše baza (ON DELETE CASCADE)
    @Transactional
    public void delete(Long id) {
        projectRepository.delete(getProject(id));
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project", id));
    }
}