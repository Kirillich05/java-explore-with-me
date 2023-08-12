package ru.practicum.event.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateRequestDto;
import ru.practicum.event.enums.EventSortType;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsPublicAccess(String text, List<Long> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, EventSortType sort, int from,
                                        int size, HttpServletRequest request);

    EventFullDto getEventByIdPublicAccess(long id, HttpServletRequest request);

    List<EventShortDto> getEventShortWithViewsAndRequests(List<Event> events);

    EventFullDto updateEventAdminAccess(long eventId, EventUpdateRequestDto updateRequestDto);

    List<EventFullDto> getEventsAdminAccess(List<Long> users, List<EventState> states, List<Long> categories,
                                            String rangeStart, String rangeEnd, int from, int size);
}
