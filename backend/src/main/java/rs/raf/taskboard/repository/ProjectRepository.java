package rs.raf.taskboard.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.taskboard.model.Project;

import java.util.Collection;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOrderByIdAsc(Limit limit);

    List<Project> findByOwnerIdOrderByIdAsc(Long ownerId);

    List<Project> findByOwnerIdInOrderByIdAsc(Collection<Long> ownerIds);
}