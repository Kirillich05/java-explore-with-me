package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    UserDto saveUser(UserDto userDto);

    void deleteUser(long userId);
}
