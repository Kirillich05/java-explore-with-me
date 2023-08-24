package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EventOfConfirmedRequests {

    Long eventId;
    Long confirmedRequests;
}
