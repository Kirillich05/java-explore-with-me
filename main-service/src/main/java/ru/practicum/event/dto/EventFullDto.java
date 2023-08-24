package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.utils.Pattern;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    long id;

    @Size(min = 20, max = 2000)
    String annotation;

    CategoryDto category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime createdOn;

    Long confirmedRequests;

    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime eventDate;

    UserShortDto initiator;
    Location location;
    Boolean paid;
    long participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime publishedOn;

    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
}
