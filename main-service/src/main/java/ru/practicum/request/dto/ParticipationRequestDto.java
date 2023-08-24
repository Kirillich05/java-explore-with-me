package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.utils.Pattern;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime created;
    Long event;
    Long requester;
    RequestStatus status;
}
