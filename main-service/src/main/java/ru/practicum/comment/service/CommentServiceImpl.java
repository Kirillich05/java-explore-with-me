package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repo;
    private final EventService eventService;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public CommentDto updateCommentByAdmin(NewCommentDto newCommentDto, long id) {
        var comment = findOrThrow(id);
        comment.setText(newCommentDto.getText());

        var updated = repo.save(comment);
        CommentDto updatedComment = commentMapper.fromModelToDto(updated);

        return updatedComment;
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(long id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public CommentDto save(NewCommentDto newCommentDto, long eventId, long userId) {
        var event = eventService.findOrThrow(eventId);
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("It is not published event");
        }

        var savedComment = repo.save(commentMapper.fromDtoToModel(user, event, newCommentDto));
        CommentDto comment = commentMapper.fromModelToDto(savedComment);
        return comment;
    }

    @Override
    public CommentDto getById(long commentId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        var comment = findOrThrow(commentId);
        isUserAuthorOfComment(commentId, userId);

        return commentMapper.fromModelToDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(long commentId, long userId) {
        isUserAuthorOfComment(commentId, userId);
        findOrThrow(commentId);
        repo.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(NewCommentDto newCommentDto, long commentId, long userId) {
        isUserAuthorOfComment(commentId, userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));

        var comment = findOrThrow(commentId);
        comment.setText(newCommentDto.getText());
        var updated = repo.save(comment);
        CommentDto updatedComment = commentMapper.fromModelToDto(updated);

        return updatedComment;
    }

    @Override
    public List<CommentDto> getCommentsByUser(long userId, Long eventId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        List<Comment> comments;

        if (eventId == null) {
            comments = repo.findAllByUserId(userId, page);
        } else {
            eventService.getEventByIdPrivateAccess(userId, eventId);
            comments = repo.findAllByUserIdAndEventId(userId, eventId, page);
        }

        return comments.stream()
                .map(commentMapper::fromModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getComments(long eventId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        eventService.findOrThrow(eventId);
        List<Comment> comments = repo.findAllByEventId(eventId, page);
        return comments.stream()
                .map(commentMapper::fromModelToDto)
                .collect(Collectors.toList());
    }

    private void isUserAuthorOfComment(long commentId, long userId) {
        var comment = findOrThrow(commentId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));

        if (userId != comment.getUser().getId()) {
            throw new ConflictException("User by id " + userId + " is not an author");
        }
    }

    private Comment findOrThrow(long id) {
        return repo.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Comment by id " + id + " was not found")
                );
    }
}
