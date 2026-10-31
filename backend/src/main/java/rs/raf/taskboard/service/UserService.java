package rs.raf.taskboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.model.User;
import rs.raf.taskboard.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ids == null znaci „svi korisnici“, inace samo izabrani
    // odabrano da bi oba kontrolera radila isto, a da logika nije duplirana
    public List<UserDto> findAll(Collection<Long> ids) {
        List<User> users = ids == null
                ? userRepository.findByOrderByIdAsc()
                : userRepository.findByIdInOrderByIdAsc(ids);
        return users.stream().map(UserDto::from).toList();
    }

    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(UserDto::from);
    }

    public Map<Long, UserDto> findByIds(Collection<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(UserDto::from)
                .collect(Collectors.toMap(UserDto::id, Function.identity()));
    }
}
