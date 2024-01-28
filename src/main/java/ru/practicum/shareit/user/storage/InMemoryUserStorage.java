package ru.practicum.shareit.user.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;
    private int id = 0;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUsersById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        if (checkEmail(user.getEmail())) {
            throw new DuplicateDataException("Пользователь с таким email уже зарегистрирован");
        }

        id++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(int id, User updatedUser) {
        User user = users.get(id);
        users.remove(id);
        if (checkEmail(updatedUser.getEmail())) {
            users.put(id, user);
            throw new DuplicateDataException("Пользователь с таким email уже зарегистрирован");
        }
        users.put(id, updatedUser);
        return updatedUser;
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
