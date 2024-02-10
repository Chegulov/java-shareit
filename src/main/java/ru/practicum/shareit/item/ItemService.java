package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.WrongAccesException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = itemMapper.createItemFromDto(userId, itemDto);
        item.setOwner(user);
        return itemMapper.getItemDto(itemStorage.save(item));
    }

    public List<ItemDto> getItems(Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        return itemStorage.findAllByOwnerId(userId).stream()
                .map(itemMapper::getItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long userId, Long id) {
        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        return itemMapper.getItemDto(itemStorage.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет.")));
    }

    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет."));
        if (item.getOwner().getId() != userId) {
            throw new WrongAccesException("У вещи с id=" + id + " другой владелец.");
        }
        item = itemMapper.updateItemFromDto(item, itemDto);

        return itemMapper.getItemDto(itemStorage.save(item));
    }

    public List<ItemDto> getItemByText(Long userId, String text) {
        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.findItemByText(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::getItemDto)
                .collect(Collectors.toList());
    }
}
