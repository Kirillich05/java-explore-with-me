package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {



    @Override
    public List<ParticipationRequestDto> getUserEventRequests(long userId, long eventId) {
        return null;
    }

    @Override
    public EventRequestStatusUpdateResult updateUserEventRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {
        return null;
    }
}
