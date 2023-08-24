package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.EventOfConfirmedRequests;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT NEW ru.practicum.event.dto.EventOfConfirmedRequests(req.event.id, count(req.id)) " +
            "FROM Request as req " +
            "WHERE req.status = 'CONFIRMED' " +
            "AND req.event.id IN (?1) " +
            "GROUP BY req.event.id"
    )
    List<EventOfConfirmedRequests> findAllConfirmedEventIds(List<Long> events);

    List<Request> findByEventId(long eventId);

    List<Request> findAllByIdIn(List<Long> reqIds);

    List<Request> findAllByRequesterId(long requesterId);

    Optional<Request> findByIdAndRequesterId(long requestId, long userId);

    Optional<Request> findByEventIdAndRequesterId(long eventId, long userId);

    @Query("SELECT req FROM Request as req " +
            "WHERE req.status = 'CONFIRMED' AND req.event.id = :eventId"
    )
    List<Request> findByEventIdConfirmedRequests(@Param("eventId") List<Long> eventId);
}
