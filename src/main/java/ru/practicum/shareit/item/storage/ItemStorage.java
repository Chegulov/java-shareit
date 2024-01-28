package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto create(int userId, Item item);

    List<ItemDto> getItems(int userId);

    ItemDto getItemById(int userId, int id);

    ItemDto update(int userId, int id, ItemDto itemDto);

    List<ItemDto> getItemByText(String text);
}
