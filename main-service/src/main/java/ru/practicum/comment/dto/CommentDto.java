package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.utils.Pattern;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    long id;

    @NotBlank
    @Size(min = 1, max = 2000)
    String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.DATE)
    LocalDateTime created;

    long eventId;
    UserShortDto user;
}
