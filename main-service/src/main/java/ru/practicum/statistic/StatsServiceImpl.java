package ru.practicum.statistic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.StatisticsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;
import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatisticsClient statisticsClient;
    private final ObjectMapper objectMapper;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        statisticsClient.addHit(endpointHitDto);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end,
                                            List<String> uris, Boolean unique) {
        ResponseEntity<Object> resp = statisticsClient.getStatistics(start, end, uris, unique);

        try {
            return Collections.singletonList(objectMapper.readValue(
                    objectMapper.writeValueAsString(resp.getBody()), ViewStatsDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        if (events.isEmpty()) {
            return views;
        }

        List<Event> publishedOnEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
        Optional<LocalDateTime> publishedDate = publishedOnEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (publishedDate.isPresent()) {
            LocalDateTime start = publishedDate.get();
            List<String> uris = publishedOnEvents.stream()
                    .map(Event::getId)
                    .map(id -> ("/events/" + id))
                    .collect(Collectors.toList());
            List<ViewStatsDto> statsDtos = getStatistics(start, LocalDateTime.now(), uris, true);
            statsDtos.forEach(statDto -> {
                Long eventId = Long.parseLong(statDto.getUri().split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + statDto.getHits());
            });
        }

        return views;
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> eventIds = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> requests = new HashMap<>();

        if (!eventIds.isEmpty()) {
            requestRepository.findAllConfirmedEventIds(eventIds)
                    .forEach(eventOfConfirmedRequests ->
                            requests.put(eventOfConfirmedRequests.getEventId(),
                                    eventOfConfirmedRequests.getConfirmedRequests()));
        }

        return requests;
    }
}
