package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.enums.EventStateAdminAction;
import ru.practicum.event.model.Location;
import ru.practicum.utils.Pattern;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateRequestDto {

    String annotation;
    Long category;
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime eventDate;

    Location location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    EventStateAdminAction stateAction;
    String title;
}
