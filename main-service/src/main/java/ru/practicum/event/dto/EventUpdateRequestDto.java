package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.enums.EventStateAdminAction;
import ru.practicum.event.model.Location;
import ru.practicum.utils.Pattern;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateRequestDto {

    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime eventDate;

    Location location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    EventStateAdminAction stateAction;

    @Size(min = 3, max = 120)
    String title;
}
