package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private UserMapper userMapper;

    private UserDto userDto;
    private UserDto userDtoToUpdate;
    private User user;

    @BeforeEach
    void init() {
        userMapper = new UserMapper();
        userDto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();
        user = userMapper.createUserFromDto(userDto);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
    }

    @Test
    void shouldCreateUser() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto createdUser = userService.create(userDto);

        Mockito.verify(userRepository).save(Mockito.any());

        assertEquals(userDto.getId(), createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        userDtoToUpdate = UserDto.builder()
                .id(1L)
                .name("UpdatedUser")
                .email("updatedUser@email.com")
                .build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userMapper.createUserFromDto(userDtoToUpdate));

        UserDto updatedUser = userService.update(1L, userDtoToUpdate);

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(userRepository).save(Mockito.any());

        assertEquals(userDtoToUpdate.getName(), updatedUser.getName());
        assertEquals(userDtoToUpdate.getEmail(), updatedUser.getEmail());
    }

    @Test
    void shouldGetUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto returnedUser = userService.getUsersById(1L);

        Mockito.verify(userRepository).findById(1L);

        assertEquals(userDto.getId(), returnedUser.getId());
        assertEquals(userDto.getName(), returnedUser.getName());
        assertEquals(userDto.getEmail(), returnedUser.getEmail());
    }

    @Test
    void shouldGetUsers() {
        List<User> users = List.of(user);

        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getUsers();

        Mockito.verify(userRepository).findAll();

        assertEquals(1, userDtos.size());
        assertEquals(userDto.getId(), userDtos.get(0).getId());
        assertEquals(userDto.getName(), userDtos.get(0).getName());
        assertEquals(userDto.getEmail(), userDtos.get(0).getEmail());
    }

    @Test
    void shouldThrowDataNotFoundException_WhenGetUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> userService.getUsersById(999L));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void shouldThrowDataNotFoundException_WhenUpdateUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> userService.update(999L, userDtoToUpdate));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void shouldThrowDataNotFoundException_WhenDeleteUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> userService.delete(999L));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }
}
