package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    Set<Long> events;
    Boolean pinned;

    @Length(min = 1, max = 50)
    String title;
}
