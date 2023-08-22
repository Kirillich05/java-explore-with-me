package ru.practicum.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable @Positive long userId) {
        log.info("Getting user requests participating in other events");
        return requestService.getParticipationRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @Positive long userId,
                                                 @RequestParam @Positive long eventId) {
        log.info("Creating request from the user by id " + userId + " to participate in the event by id " + eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("Cancelling request " + requestId + " of user " + userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
