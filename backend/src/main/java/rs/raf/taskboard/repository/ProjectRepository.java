package rs.raf.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.taskboard.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
