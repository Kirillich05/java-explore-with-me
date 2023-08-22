package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    Set<Long> events;
    Boolean pinned = false;

    @NotBlank
    @Length(min = 1, max = 50)
    String title;
}
