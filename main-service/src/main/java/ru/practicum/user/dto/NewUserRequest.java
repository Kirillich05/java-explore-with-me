package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank
    @Length(min = 2, max = 250)
    String name;

    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    String email;
}
