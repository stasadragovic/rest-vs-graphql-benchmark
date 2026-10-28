package rs.raf.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.taskboard.model.Task;

import java.util.Collection;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectIdOrderByIdAsc(Long projectId);

    List<Task> findByProjectIdInOrderByIdAsc(Collection<Long> projectIds);
}
