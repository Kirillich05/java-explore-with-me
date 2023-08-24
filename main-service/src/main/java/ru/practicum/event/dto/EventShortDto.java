package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.utils.Pattern;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    long id;
    String annotation;
    CategoryDto category;
    long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime eventDate;

    UserShortDto initiator;
    boolean paid;
    String title;
    long views;
}
