package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.model.RequestStatusAction;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.statistic.StatsService;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repo;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final StatsService statsService;
    private final RequestMapper requestMapper;


    @Override
    public List<ParticipationRequestDto> getUserEventRequests(long userId, long eventId) {
        eventService.findOrThrow(eventId);
        return repo.findByEventId(eventId).stream()
                .map(requestMapper::fromModelToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateUserEventRequestStatus(long userId, long eventId,
                                                                       EventRequestStatusUpdateRequest updateRequest) {
        var event = eventService.findOrThrow(eventId);
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User was not found")
        );

        if (userId != event.getInitiator().getId()) {
            throw new ConflictException("User is not initiator");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> confirmedRequests = new ArrayList<>();
        var requests = repo.findAllByIdIn(updateRequest.getRequestIds());

        if (!requests.stream().map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("Request must have status PENDING");
        }

        Long limit = event.getParticipantLimit() - statsService.getConfirmedRequests(List.of(event))
                .getOrDefault(eventId, 0L);
        if (limit <= 0 && event.getParticipantLimit() != 0) {
            throw new ConflictException("The participant limit has been reached");
        }

        if (updateRequest.getStatus().equals(RequestStatusAction.REJECTED)) {
            rejectedRequests.addAll(changeRequestsStatus(requests, RequestStatus.REJECTED));
        } else {
            confirmedRequests.addAll(changeRequestsStatus(requests, RequestStatus.CONFIRMED));
        }
        var result = new EventRequestStatusUpdateResult(
                confirmedRequests.stream().map(requestMapper::fromModelToParticipationRequestDto).collect(Collectors.toList()),
                rejectedRequests.stream().map(requestMapper::fromModelToParticipationRequestDto).collect(Collectors.toList())
        );

        return result;
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        return  repo.findAllByRequesterId(userId).stream()
                .map(requestMapper::fromModelToParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        var event = eventService.findOrThrow(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("Can not create request for your own event");
        }

        Optional<Request> requestForChecking = repo.findByEventIdAndRequesterId(eventId, user.getId());
        if (requestForChecking.isPresent()) {
            throw new ConflictException("Request is existed");
        }

        if (event.getParticipantLimit() != 0 &&
                (statsService.getConfirmedRequests(List.of(event)).getOrDefault(
                        eventId, 0L) + 1) > event.getParticipantLimit()) {
            throw new ConflictException("participant limit is achieved");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        var savedRequest = repo.save(request);
        ParticipationRequestDto savedRequestDto = requestMapper.fromModelToParticipationRequestDto(savedRequest);
        return savedRequestDto;
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        var request = repo.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("Request is not found")
        );
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.fromModelToParticipationRequestDto(repo.save(request));
    }

    private List<Request> changeRequestsStatus(List<Request> requests, RequestStatus status) {
        requests.forEach(request -> request.setStatus(status));
        return repo.saveAll(requests);
    }
}
