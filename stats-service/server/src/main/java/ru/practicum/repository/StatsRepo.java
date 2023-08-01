package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT NEW ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.uri IN ?3 AND eh.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT NEW ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.uri IN ?3 AND eh.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);


    @Query("SELECT NEW ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStats> getUniqueStatsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getStatsWithoutUris(LocalDateTime start, LocalDateTime end);
}
