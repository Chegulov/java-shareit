package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
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
        return userStorage.getUsers().stream().map(userMapper::getUserDto).collect(Collectors.toList());
    }

    public UserDto getUsersById(int id) {
        return userMapper.getUserDto(userStorage.getUsersById(id).
                orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден.")));
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.createUserFromDto(userDto);
        return userMapper.getUserDto(userStorage.create(user));
    }

    public UserDto update(int id, UserDto userDto) {
        User user = User.getCopy(userStorage.getUsersById(id).
                orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден.")));
        user = userMapper.updateUserFromDto(user, userDto);
        return userMapper.getUserDto(userStorage.update(id, user));
    }

    public void delete(int id) {
        userStorage.delete(id);
    }
}
