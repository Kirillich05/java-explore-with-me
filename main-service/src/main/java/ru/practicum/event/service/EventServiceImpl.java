package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateRequestDto;
import ru.practicum.event.enums.EventSortType;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.statistic.StatsHitMapper;
import ru.practicum.statistic.StatsService;
import ru.practicum.utils.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StatsService statsService;
    private final ModelMapper mapper;
    private final EntityManager entityManager;

    @Override
    public List<EventShortDto> getEventsPublicAccess(String text, List<Long> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, EventSortType sort, int from,
                                               int size, HttpServletRequest request) {
        var start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(Pattern.DATE));
        var end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(Pattern.DATE));

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate predicate = criteriaBuilder.conjunction();

        if (text != null && !text.isBlank()) {
            var annotation = criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" +
                    text.toLowerCase() + "%");
            var description = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" +
                    text.toLowerCase() + "%");
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(annotation, description));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("category").in(categories));
        }

        if (paid != null) {
            predicate = criteriaBuilder.and(predicate, root.get("paid").in(paid));
        }

        if (rangeEnd != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("evenDate")
                    .as(LocalDateTime.class), end));
        }
        if (rangeStart != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("evenDate")
                    .as(LocalDateTime.class), start));
        }

        query.select(root).where(predicate);
        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> participantLimit = new HashMap<>();
        events.forEach(event -> participantLimit.put(event.getId(), event.getParticipantLimit()));
        List<EventShortDto> eventShortDtos = getEventShortWithViewsAndRequests(events);

        if (onlyAvailable) {
            eventShortDtos = eventShortDtos.stream()
                    .filter(event -> (participantLimit.get(event.getId()) == 0 ||
                            participantLimit.get(event.getId()) > event.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals(EventSortType.VIEWS)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
        } else {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        statsService.addHit(StatsHitMapper.toEndpointHit(request, "main-service"));

        return eventShortDtos;
    }

    @Override
    public EventFullDto getEventByIdPublicAccess(long id, HttpServletRequest request) {
        var event = findOrThrow(id);
        statsService.addHit(StatsHitMapper.toEndpointHit(request, "main-service"));
        List<EventFullDto> eventFullDtos = mapEventToViewAndRequests(List.of(event));
        return eventFullDtos.get(0);
    }

    @Override
    public List<EventShortDto> getEventShortWithViewsAndRequests(List<Event> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> requests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map(event -> convertToEventShortDto(event,
                        requests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdminAccess(long eventId, EventUpdateRequestDto updateRequestDto) {
        return null;
    }

    @Override
    public List<EventFullDto> getEventsAdminAccess(List<Long> users, List<EventState> states, List<Long> categories,
                                                   String rangeStart, String rangeEnd, int from, int size) {
        return null;
    }

    private Event findOrThrow(long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Event by id " + id + " was not found")
                );
    }

    private List<EventFullDto> mapEventToViewAndRequests(List<Event> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> requests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map(event -> convertToEventFullDto(event,
                        requests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private EventFullDto convertToEventFullDto(Event event) {
        return mapper.map(event, EventFullDto.class);
    }

    private EventFullDto convertToEventFullDto(Event event, Long requests, Long views) {
        var eventFullDto = mapper.map(event, EventFullDto.class);
        eventFullDto.setViews(views);
        eventFullDto.setConfirmedRequests(requests);
        return eventFullDto;
    }

    private EventShortDto convertToEventShortDto(Event event, Long requests, Long views) {
        var eventShortDto = mapper.map(event, EventShortDto.class);
        eventShortDto.setViews(views);
        eventShortDto.setConfirmedRequests(requests);
        return eventShortDto;
    }
}
