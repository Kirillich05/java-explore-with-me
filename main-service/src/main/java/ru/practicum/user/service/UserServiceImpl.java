package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final UserMapper userMapper;



    @Override
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            return repo.findAll(page).stream()
                    .map(userMapper::fromModelToUserDto)
                    .collect(Collectors.toList());
        } else {
            return repo.findAllByIdIn(ids, page).stream()
                    .map(userMapper::fromModelToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public UserDto saveUser(NewUserRequest userDto) {
        if (repo.existsByName(userDto.getName())) {
            throw new ConflictException("User with the name " + userDto.getName() + " is existed");
        }

        var user = userMapper.fromNewUserRequestToModel(userDto);
        return userMapper.fromModelToUserDto(repo.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        repo.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        repo.deleteById(userId);
    }
}
