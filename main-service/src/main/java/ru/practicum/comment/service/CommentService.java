package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto updateCommentByAdmin(NewCommentDto newCommentDto, long id);

    void deleteCommentByAdmin(long id);

    CommentDto save(NewCommentDto newCommentDto, long eventId, long userId);

    CommentDto getById(long commentId, long userID);

    void deleteCommentByUser(long commentId, long userId);

    CommentDto updateCommentByUser(NewCommentDto newCommentDto, long commentId, long userId);

    List<CommentDto> getCommentsByUser(long userId, Long eventId, int from, int size);

    List<CommentDto> getComments(long eventId, int from, int size);
}
