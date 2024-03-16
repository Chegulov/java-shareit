package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    private UserDto userDto;
    private UserDto userDtoToUpdate;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();
    }

    @Test
    void shouldCreateUser() {
        UserDto createdUser = userService.create(userDto);

        assertEquals(userDto.getId(), createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        userService.create(userDto);
        userDtoToUpdate = UserDto.builder()
                .id(1L)
                .name("UpdatedUser")
                .email("updatedUser@email.com")
                .build();

        UserDto updatedUser = userService.update(1L, userDtoToUpdate);

        assertEquals(userDtoToUpdate.getName(), updatedUser.getName());
        assertEquals(userDtoToUpdate.getEmail(), updatedUser.getEmail());
    }

    @Test
    void shouldGetUser() {
        userService.create(userDto);

        UserDto returnedUser = userService.getUsersById(1L);

        assertEquals(userDto.getId(), returnedUser.getId());
        assertEquals(userDto.getName(), returnedUser.getName());
        assertEquals(userDto.getEmail(), returnedUser.getEmail());
    }

    @Test
    void shouldGetUsers() {
        userService.create(userDto);
        List<UserDto> userDtos = userService.getUsers();

        assertEquals(1, userDtos.size());
        assertEquals(userDto.getId(), userDtos.get(0).getId());
        assertEquals(userDto.getName(), userDtos.get(0).getName());
        assertEquals(userDto.getEmail(), userDtos.get(0).getEmail());
    }

    @Test
    void shouldThrowDataNotFoundException_WhenGetUserNotExist() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> userService.getUsersById(999L));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void shouldThrowDataNotFoundException_WhenUpdateUserNotExist() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> userService.update(999L, userDtoToUpdate));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void shouldThrowDataNotFoundException_WhenDeleteUserNotExist() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> userService.delete(999L));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }
}
