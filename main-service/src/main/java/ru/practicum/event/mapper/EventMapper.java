package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;
import ru.practicum.utils.Pattern;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(category)")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())", dateFormat = Pattern.DATE)
    @Mapping(target = "initiator", expression = "java(initiator)")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", constant = "PENDING")
    Event fromNewEventDtoToEvent(NewEventDto newEventDto, Category category, User initiator);

    EventShortDto fromModelToEventShortDto(Event event, Long confirmedRequests, Long views);

    EventFullDto fromModelToFullEventDto(Event event, Long confirmedRequests, Long views);


}
