package rs.raf.taskboard.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import rs.raf.taskboard.dto.CommentDto;
import rs.raf.taskboard.dto.CreateCommentRequest;
import rs.raf.taskboard.dto.UpdateCommentRequest;
import rs.raf.taskboard.exception.NotFoundException;
import rs.raf.taskboard.model.Comment;
import rs.raf.taskboard.repository.CommentRepository;
import rs.raf.taskboard.repository.TaskRepository;
import rs.raf.taskboard.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public CommentDto create(Long taskId, Long authorId, @Valid CreateCommentRequest request) {
        Comment comment = new Comment();
        comment.setTask(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", taskId)));
        comment.setAuthor(userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User", authorId)));
        comment.setText(request.text());
        return CommentDto.from(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto update(Long id, @Valid UpdateCommentRequest request) {
        Comment comment = getComment(id);
        comment.setText(request.text());
        return CommentDto.from(comment);
    }

    @Transactional
    public void delete(Long id) {
        commentRepository.delete(getComment(id));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment", id));
    }
}