package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    List<ItemDto> getItems(Long userId);

    ItemDto getItemById(Long userId, Long id);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    List<ItemDto> getItemByText(Long userId, String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
