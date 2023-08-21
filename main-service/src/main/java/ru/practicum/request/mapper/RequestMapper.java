package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        uses = {EventMapper.class, UserMapper.class})
public interface RequestMapper {

    @Mapping(target = "event", expression = "java(request.getEvent().getId())")
    @Mapping(target = "requester", expression = "java(request.getRequester().getId())")
    ParticipationRequestDto fromModelToParticipationRequestDto(Request request);
}
