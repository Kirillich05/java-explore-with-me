package ru.practicum.request.service;

import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateUserEventRequestStatus(long userId, long eventId,
                                                                EventRequestStatusUpdateRequest updateRequest);
}
