package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto fromModelToCompilationDto(Compilation compilation, List<EventShortDto> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation fromNewCompilationDtoToModel(NewCompilationDto newCompilationDto, Set<Event> events);

    CompilationDto fromModelToCompilationDto(Compilation compilation);
}
