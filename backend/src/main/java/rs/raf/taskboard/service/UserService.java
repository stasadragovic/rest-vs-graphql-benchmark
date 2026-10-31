package rs.raf.taskboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.repository.UserRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(UserDto::from);
    }

    public Map<Long, UserDto> findByIds(Collection<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(UserDto::from)
                .collect(Collectors.toMap(UserDto::id, Function.identity()));
    }
}
