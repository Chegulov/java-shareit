package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}
