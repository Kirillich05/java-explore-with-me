package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventId(long eventId, Pageable page);

    List<Comment> findAllByUserId(long userId, Pageable page);

    List<Comment> findAllByUserIdAndEventId(long userId, Long eventId, Pageable page);
}
