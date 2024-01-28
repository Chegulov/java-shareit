package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(int userId, Item item);

    List<Item> getItems(int userId);

    Optional<Item> getItemById(int id);

    Item update(int userId, int id, Item item);

    List<Item> getItemByText(String text);
}
