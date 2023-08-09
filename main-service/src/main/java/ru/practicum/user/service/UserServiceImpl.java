package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final ModelMapper mapper;



    @Override
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        if (!ids.isEmpty()) {
            return repo.findAllByIdIn(ids, page).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return repo.findAll(page).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        var user = convertToModel(userDto);
        return convertToDto(repo.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        repo.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        repo.deleteById(userId);
    }

    private UserDto convertToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private User convertToModel(UserDto dto) {
        return mapper.map(dto, User.class);
    }
}
