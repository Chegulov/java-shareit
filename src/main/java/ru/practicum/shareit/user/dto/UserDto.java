package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private int id;
    @Email
    private String email;
    private String name;
}
