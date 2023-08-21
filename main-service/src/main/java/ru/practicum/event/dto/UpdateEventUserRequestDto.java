package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.enums.UserStateAction;
import ru.practicum.event.model.Location;
import ru.practicum.utils.Pattern;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequestDto {

    @Length(min = 20, max = 2000)
    String annotation;
    Long category;

    @Length(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime eventDate;

    Location location;

    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    UserStateAction stateAction;

    @Length(min = 3, max = 120)
    String title;
}
