package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventSortType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {


    @Override
    public List<EventShortDto> getEventsPublicAccess(String text, List<Long> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, EventSortType sort, int from,
                                               int size, HttpServletRequest request) {
        return null;
    }

    @Override
    public EventFullDto getEventByIdPublicAccess(long id, HttpServletRequest request) {
        return null;
    }
}
