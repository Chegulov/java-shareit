package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::getUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUsersById(Long id) {
        return userMapper.getUserDto(userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден.")));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.createUserFromDto(userDto);
        return userMapper.getUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден."));

        user = userMapper.updateUserFromDto(user, userDto);
        return userMapper.getUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден."));

        userRepository.deleteById(id);
    }

}
