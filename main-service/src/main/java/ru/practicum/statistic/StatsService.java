package ru.practicum.statistic;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatsService {

    void addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end,
                                     List<String> uris, Boolean unique);

    Map<Long, Long> getViews(List<Event> events);

    Map<Long, Long> getConfirmedRequests(List<Event> events);
}
