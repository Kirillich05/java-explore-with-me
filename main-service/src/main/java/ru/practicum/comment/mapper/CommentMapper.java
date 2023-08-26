package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    CommentDto fromModelToDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "event", expression = "java(event)")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment fromDtoToModel(User user, Event event, NewCommentDto newCommentDto);
}
