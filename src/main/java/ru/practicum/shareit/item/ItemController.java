package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validateGroups.Create;
import ru.practicum.shareit.validateGroups.Update;

import java.util.List;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) int userId, @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(USER_ID) int userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(USER_ID) int userId, @PathVariable int id) {
        return itemService.getItemById(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(USER_ID) int userId, @PathVariable int id, @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestHeader(USER_ID) int userId, @RequestParam (value = "text") String text) {
        return itemService.getItemByText(userId, text);
    }
}
