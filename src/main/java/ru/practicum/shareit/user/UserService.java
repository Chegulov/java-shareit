package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public List<UserDto> getUsers() {
        return userStorage.findAll().stream().map(userMapper::getUserDto).collect(Collectors.toList());
    }

    public UserDto getUsersById(Long id) {
        return userMapper.getUserDto(userStorage.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден.")));
    }

    public UserDto create(UserDto userDto) {
        if (userStorage.findByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateDataException("Пользователь с таким email уже зарегистрирован");
        }
        User user = userMapper.createUserFromDto(userDto);
        return userMapper.getUserDto(userStorage.save(user));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден."));

        user = userMapper.updateUserFromDto(user, userDto);
        return userMapper.getUserDto(userStorage.save(user));
    }

    public void delete(Long id) {
        userStorage.deleteById(id);
    }

}
