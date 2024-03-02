package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UserDto user2Dto;
    private UserDto userDtoToUpdate;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();

        user2Dto = UserDto.builder()
                .id(2L)
                .name("SecondUser")
                .email("secondUser@email.com")
                .build();
    }

    @Test
    @SneakyThrows
    void create_Status200AndReturnedUser_WhenAllOk() {
        Mockito.when(userService.create(Mockito.any())).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(userService).create(userDto);

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void create_Status400AndReturnedUser_WhenNameBlank() {
        userDto.setName(null);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(userDto);
    }

    @Test
    @SneakyThrows
    void create_Status400AndReturnedUser_WhenEmailBlank() {
        userDto.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(userDto);
    }

    @Test
    @SneakyThrows
    void create_Status400AndReturnedUser_WhenWrongEmail() {
        userDto.setEmail("usermail.com");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(userDto);
    }

    @Test
    @SneakyThrows
    void getUsersById_Status200() {
        Mockito.when(userService.getUsersById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));

        Mockito.verify(userService).getUsersById(1L);
    }

    @Test
    @SneakyThrows
    void getUsers_Status200() {

        Mockito.when(userService.getUsers()).thenReturn(List.of(userDto, user2Dto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto, user2Dto))));

        Mockito.verify(userService).getUsers();
    }

    @Test
    @SneakyThrows
    void update_Status200AndReturnedUser_WhenAllOk() {
        userDtoToUpdate = UserDto.builder()
                .id(1L)
                .name("UpdatedUser")
                .email("updatedUser@Email.com")
                .build();

        Mockito.when(userService.update(1L, userDtoToUpdate)).thenReturn(userDtoToUpdate);

        String result = mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(userService).update(1L, userDtoToUpdate);

        assertEquals(objectMapper.writeValueAsString(userDtoToUpdate), result);
    }

    @Test
    @SneakyThrows
    void update_Status400_WhenWrongEmail() {
        userDtoToUpdate = UserDto.builder()
                .id(1L)
                .name("UpdatedUser")
                .email("updatedUserEmail.com")
                .build();

        Mockito.when(userService.update(1L, userDtoToUpdate)).thenReturn(userDtoToUpdate);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).update(1L, userDtoToUpdate);
    }

    @Test
    @SneakyThrows
    void delete_Status200() {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(1L);
    }
}
