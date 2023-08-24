package ru.practicum.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                  @PathVariable @Positive long eventId,
                                  @PathVariable @Positive long userId) {
        log.info("Create comment for event " + eventId + " by user " + userId);
        return service.save(newCommentDto, eventId, userId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable @Positive long commentId,
                              @PathVariable @Positive long userId) {
        log.info("Get comment by " + commentId + " by user " + userId);
        return service.getById(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                  @PathVariable @Positive long commentId,
                                  @PathVariable @Positive long userId) {
        log.info("Update comment " + commentId + " by user " + userId);
        return service.updateCommentByUser(newCommentDto, commentId, userId);
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable @Positive long userId,
                                        @RequestParam(required = false) @Positive Long eventId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get comments of event " + eventId + " by user " + userId);
        return service.getCommentsByUser(userId, eventId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive long commentId,
                              @PathVariable @Positive long userId) {
        log.info("Delete comment by id " + commentId + " by user " + userId);
        service.deleteCommentByUser(commentId, userId);
    }
}
