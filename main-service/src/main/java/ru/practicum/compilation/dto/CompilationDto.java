package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    Long id;
    List<EventShortDto> events;
    Boolean pinned;
    String title;
}
