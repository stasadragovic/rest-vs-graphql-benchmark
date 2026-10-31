package rs.raf.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.taskboard.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByOrderByIdAsc();

    List<User> findByIdInOrderByIdAsc(Collection<Long> ids);
}
