package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<UserDto> getUsers() {
        return userStorage.getUsers();
    }

    public UserDto getUsersById(int id) {
        return userStorage.getUsersById(id);
    }

    public UserDto create(User user) {
        return userStorage.create(user);
    }

    public UserDto update(int id, UserDto userDto) {
        return userStorage.update(id, userDto);
    }

    public void delete(int id) {
        userStorage.delete(id);
    }
}
