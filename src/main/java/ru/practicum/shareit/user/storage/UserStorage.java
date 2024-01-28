package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<UserDto> getUsers();

    UserDto getUsersById(int id);

    UserDto create(User user);

    UserDto update(int id, UserDto userDto);

    void delete(int id);
}
