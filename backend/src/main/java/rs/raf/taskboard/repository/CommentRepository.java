package rs.raf.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.taskboard.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTaskIdOrderByIdAsc(Long taskId);

    List<Comment> findByTaskIdInOrderByIdAsc(Collection<Long> taskIds);
}
