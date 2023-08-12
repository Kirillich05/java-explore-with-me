package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.EventOfConfirmedRequests;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT NEW ru.practicum.event.dto.EventOfConfirmedRequests(req.event.id, count(req.id)) " +
            "FROM Request as req " +
            "WHERE req.status = 'CONFIRMED' " +
            "AND req.event.id IN (?1) " +
            "GROUP BY req.event.id"
    )
    List<EventOfConfirmedRequests> findAllConfirmedEventIds(List<Long> events);
}
