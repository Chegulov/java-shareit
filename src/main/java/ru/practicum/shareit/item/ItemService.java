package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto create(int userId, Item item) {
        userStorage.getUsersById(userId);
        return itemStorage.create(userId, item);
    }

    public List<ItemDto> getItems(int userId) {
        userStorage.getUsersById(userId);
        return itemStorage.getItems(userId);
    }

    public ItemDto getItemById(int userId, int id) {
        userStorage.getUsersById(id);
        return itemStorage.getItemById(userId, id);
    }

    public ItemDto update(int userId, int id, ItemDto itemDto) {
        userStorage.getUsersById(userId);
        return itemStorage.update(userId, id, itemDto);
    }

    public List<ItemDto> getItemByText(int userId, String text) {
        userStorage.getUsersById(userId);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemByText(text);
    }
}
