package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService service;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsPrivateAccess(@PathVariable @Positive long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Getting events from private access");
        return service.getEventsPrivateAccess(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Creating event");
        return service.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable("userId") @Positive long userId,
                                       @PathVariable("eventId") @Positive long eventId) {
        log.info("Getting event " + eventId + " from private access");
        return service.getEventByIdPrivateAccess(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable("userId") @Positive long userId,
                                          @PathVariable("eventId") @Positive long eventId,
                                          @RequestBody @Valid UpdateEventUserRequestDto requestDto) {
        log.info("Updating event " + eventId + " from private access");
        return service.updateEventPrivateAccess(userId, eventId, requestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable("userId") @Positive long userId,
                                                              @PathVariable("eventId") @Positive long eventId) {
        log.info("Getting user event requests from private access");
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable("userId") @Positive long userId,
                                                                    @PathVariable("eventId") @Positive long eventId,
                                                                    @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        log.info("Updating user event request " + eventId + " from private access");
        return requestService.updateUserEventRequestStatus(userId, eventId, updateRequest);
    }
}
