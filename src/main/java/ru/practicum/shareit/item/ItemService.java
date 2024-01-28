package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.WrongAccesException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    public ItemDto create(int userId, ItemDto itemDto) {
        userStorage.getUsersById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = itemMapper.createItemFromDto(userId, itemDto);
        return itemMapper.getItemDto(itemStorage.create(userId, item));
    }

    public List<ItemDto> getItems(int userId) {
        userStorage.getUsersById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        return itemStorage.getItems(userId).stream()
                .map(itemMapper::getItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(int userId, int id) {
        userStorage.getUsersById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        return itemMapper.getItemDto(itemStorage.getItemById(id).
                orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет.")));
    }

    public ItemDto update(int userId, int id, ItemDto itemDto) {
        userStorage.getUsersById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = Item.getCopy(itemStorage.getItemById(id).
                orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет.")));
        if (item.getOwnerId() != userId) {
            throw new WrongAccesException("У вещи с id=" + id + " другой владелец.");
        }
        item = itemMapper.updateItemFromDto(item, itemDto);

        return itemMapper.getItemDto(itemStorage.update(userId, id, item));
    }

    public List<ItemDto> getItemByText(int userId, String text) {
        userStorage.getUsersById(userId);
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.getItemByText(text).stream()
                .map(itemMapper::getItemDto)
                .collect(Collectors.toList());
    }
}
