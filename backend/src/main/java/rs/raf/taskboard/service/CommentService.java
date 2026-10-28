package rs.raf.taskboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.taskboard.dto.CommentDto;
import rs.raf.taskboard.repository.CommentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentDto> findByTask(Long taskId) {
        return commentRepository.findByTaskIdOrderByIdAsc(taskId).stream()
                .map(CommentDto::from)
                .toList();
    }

    public Map<Long, List<CommentDto>> findByTasks(Collection<Long> taskIds) {
        return commentRepository.findByTaskIdInOrderByIdAsc(taskIds).stream()
                .map(CommentDto::from)
                .collect(Collectors.groupingBy(CommentDto::taskId));
    }
}
