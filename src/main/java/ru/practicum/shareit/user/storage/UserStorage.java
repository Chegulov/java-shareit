package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getUsers();

    Optional<User> getUsersById(int id);

    User create(User user);

    User update(int id, User user);

    void delete(int id);
}
