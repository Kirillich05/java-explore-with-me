package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventSortType;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.EventStateAdminAction;
import ru.practicum.event.enums.UserStateAction;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.statistic.StatsHitMapper;
import ru.practicum.statistic.StatsService;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
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
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StatsService statsService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final EntityManager entityManager;
    private final LocationRepository locationRepository;

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
    @Transactional
    public EventFullDto updateEventAdminAccess(long eventId, EventUpdateRequestDto updateRequestDto) {
        var event = findOrThrow(eventId);
        if (updateRequestDto.getEventDate() != null) {
            var eventTime = updateRequestDto.getEventDate();
            if (eventTime.isBefore(LocalDateTime.now()) || eventTime.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new ConflictException("The time is wrong");
            }
            event.setEventDate(updateRequestDto.getEventDate());
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Event is published or cancelled");
        }
        if (updateRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateRequestDto.getAnnotation());
        }
        if (updateRequestDto.getCategory() != null) {
            var category = categoryRepository.findById(updateRequestDto.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category is not existed"));
            event.setCategory(category);
        }
        if (updateRequestDto.getDescription() != null) {
            event.setDescription(updateRequestDto.getDescription());
        }
        if (updateRequestDto.getLocation() != null) {
            event.setLocation(updateRequestDto.getLocation());
        }
        if (updateRequestDto.getPaid() != null) {
            event.setPaid(updateRequestDto.getPaid());
        }
        if (updateRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequestDto.getParticipantLimit());
        }
        if (updateRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateRequestDto.getRequestModeration());
        }
        if (updateRequestDto.getTitle() != null) {
            event.setTitle(updateRequestDto.getTitle());
        }

        if (updateRequestDto.getStateAction() != null) {
            if (updateRequestDto.getStateAction().equals(EventStateAdminAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateRequestDto.getStateAction().equals(EventStateAdminAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }

        eventRepository.save(event);
        var updatedEvent = mapEventToViewAndRequests(List.of(event)).get(0);
        return updatedEvent;
    }

    @Override
    public List<EventFullDto> getEventsAdminAccess(List<Long> users, List<EventState> states, List<Long> categories,
                                                   String rangeStart, String rangeEnd, int from, int size) {
        var start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(Pattern.DATE));
        var end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(Pattern.DATE));

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate predicate = criteriaBuilder.conjunction();

        if (categories != null && !categories.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("category").in(categories));
        }

        if (states != null && !states.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("state").in(states));
        }

        if (users != null && !users.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("initiator").in(users));
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

        return mapEventToViewAndRequests(events);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        var category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category is not found"));
        var initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Wrong time");
        }

        newEventDto.setLocation(locationRepository.save(newEventDto.getLocation()));

        var event = eventRepository.save(convertNewEventDtoToEventModel(
                newEventDto, category, initiator));
        EventFullDto eventFullDto = convertToEventFullDto(event, 0L, 0L);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEventsPrivateAccess(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        var events = eventRepository.findAllByInitiatorId(userId, page).stream()
                .collect(Collectors.toList());

        return getEventShortWithViewsAndRequests(events);
    }

    @Override
    public EventFullDto getEventByIdPrivateAccess(long userId, long eventId) {
        var event = findEventByInitiator(eventId, userId);
        return mapEventToViewAndRequests(List.of(event)).get(0);
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivateAccess(long userId, long eventId, UpdateEventUserRequestDto requestDto) {
        var event = eventRepository.findByIdAndInitiator(userId, eventId).orElseThrow(
                () -> new NotFoundException("Event was not found")
        );

        if (requestDto.getEventDate() != null && requestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event started in less 2 h");
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is published");
        }

        if (requestDto.getStateAction() != null) {
            if (requestDto.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else {
                event.setState(EventState.PENDING);
            }
        }

        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getCategory() != null) {
            var category = categoryRepository.findById(requestDto.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (requestDto.getDescription() != null) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getEventDate() != null) {
            event.setEventDate(requestDto.getEventDate());
        }
        if (requestDto.getLocation() != null) {
            var location = locationRepository.save(requestDto.getLocation());
            event.setLocation(location);
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getRequestModeration() != null) {
            event.setRequestModeration(requestDto.getRequestModeration());
        }
        if (requestDto.getTitle() != null) {
            event.setTitle(requestDto.getTitle());
        }

        eventRepository.save(event);
        return mapEventToViewAndRequests(List.of(event)).get(0);
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
    public Event findOrThrow(long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Event by id " + id + " was not found")
                );
    }

    private Event findEventByInitiator(long eventId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        return eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event is not found"));
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

    private Event convertNewEventDtoToEventModel(NewEventDto newEventDto, Category category,
                                                 User initiator) {
        var event = mapper.map(newEventDto, Event.class);
        event.setCategory(category);
        event.setInitiator(initiator);
        return event;
    }

    private EventShortDto convertToEventShortDto(Event event, Long requests, Long views) {
        var eventShortDto = mapper.map(event, EventShortDto.class);
        eventShortDto.setViews(views);
        eventShortDto.setConfirmedRequests(requests);
        return eventShortDto;
    }
}
