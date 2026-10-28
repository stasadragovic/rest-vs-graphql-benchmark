package rs.raf.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.taskboard.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
