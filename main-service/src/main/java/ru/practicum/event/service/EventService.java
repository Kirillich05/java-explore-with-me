package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventSortType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsPublicAccess(String text, List<Long> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, EventSortType sort, int from,
                                        int size, HttpServletRequest request);

    EventFullDto getEventByIdPublicAccess(long id, HttpServletRequest request);
}
