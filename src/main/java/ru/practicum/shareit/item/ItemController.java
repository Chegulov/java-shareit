package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody Item item) {
        return itemService.create(userId, item);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        return itemService.getItemById(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id, @Valid @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam (value = "text") String text) {
        return itemService.getItemByText(userId, text);
    }
}
