package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.WrongAccesException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> items;
    private final ItemMapper itemMapper;
    private int id = 0;

    @Override
    public ItemDto create(int userId, Item item) {
        id++;
        item.setId(id);
        item.setOwnerId(userId);
        items.put(id, item);
        return itemMapper.getItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(itemMapper :: getItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(int userId, int id) {
        return itemMapper.getItemDto(items.get(id));
    }

    @Override
    public ItemDto update(int userId, int id, ItemDto itemDto) {
        if (items.get(id).getOwnerId() != userId) {
           throw new WrongAccesException("У вещи с id=" + id + " другой владелец.");
        }

        Item item = items.get(id);
        item = itemMapper.updateItemFromDto(item, itemDto);
        items.put(id, item);
        return itemMapper.getItemDto(item);
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        String finalText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(finalText)
                        || item.getName().toLowerCase().contains(finalText)))
                .filter(Item::getAvailable)
                .map(itemMapper :: getItemDto)
                .collect(Collectors.toList());
    }
}
