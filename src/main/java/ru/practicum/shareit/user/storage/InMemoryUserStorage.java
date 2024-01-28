package ru.practicum.shareit.user.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;
    private final UserMapper userMapper;
    private int id = 0;

    @Override
    public List<UserDto> getUsers() {
        return users.values().stream().map(userMapper::getUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUsersById(int id) {
        if (!users.containsKey(id)) {
            throw new DataNotFoundException("Пользователь с id=" + id + " не найден.");
        }
        return userMapper.getUserDto(users.get(id));
    }

    @Override
    public UserDto create(User user) {
        if (checkEmail(user.getEmail())) {
            throw new DuplicateDataException("Пользователь с таким email уже зарегистрирован");
        }

        id++;
        user.setId(id);
        users.put(id, user);
        return userMapper.getUserDto(user);
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
        if (!users.containsKey(id)) {
            throw new DataNotFoundException("Пользователь с id=" + id + " не найден.");
        }
        User user = users.get(id);
        users.remove(id);
        if (checkEmail(userDto.getEmail())) {
            users.put(id, user);
            throw new DuplicateDataException("Пользователь с таким email уже зарегистрирован");
        }
        user = userMapper.updateUserFromDto(user, userDto);
        users.put(id, user);
        return userMapper.getUserDto(user);
    }

    @Override
    public void delete(int id) {
        if (!users.containsKey(id)) {
            throw new DataNotFoundException("Пользователь с id=" + id + " не найден.");
        }
        users.remove(id);
    }

    private boolean checkEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
