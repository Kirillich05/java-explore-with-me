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

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService service;

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable @Positive long commentId) {
        log.info("Updating comment by id " + commentId + " by admin");
        return service.updateCommentByAdmin(newCommentDto, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive long commentId) {
        log.info("Delete comment by id " + commentId + " by admin");
        service.deleteCommentByAdmin(commentId);
    }
}
