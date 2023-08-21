package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(long userId, Pageable page);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    Optional<Event> findByIdAndPublishedOnIsNotNull(long eventId);

    @Query("SELECT event from Event event " +
            "WHERE event.id IN (:ids)"
    )
    List<Event> findByIds(@Param("ids") List<Long> ids);

    Boolean existsByCategoryId(Long id);
}
