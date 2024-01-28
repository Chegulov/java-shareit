package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        return userService.getUsersById(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id , @Valid @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }
}
