package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(USER_ID) Long userId,
                                  @RequestParam(value = "from", defaultValue = "0") Integer start,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.getItems(userId, start, size);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(USER_ID) Long userId, @PathVariable Long id) {
        return itemService.getItemById(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(USER_ID) Long userId, @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestHeader(USER_ID) Long userId, @RequestParam (value = "text") String text,
                                       @RequestParam(value = "from", defaultValue = "0") Integer start,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.getItemByText(userId, text, start, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
