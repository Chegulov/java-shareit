//package ru.practicum.shareit.item.storage;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@Component
//public class InMemoryItemStorage implements ItemStorage {
//    private final Map<Integer, Item> items;
//    private final Map<Integer, Map<Integer, Item>> userItemIndex;
//    private int id = 0;
//
//    @Override
//    public Item create(int userId, Item item) {
//        id++;
//        item.setId(id);
//        item.setU(userId);
//        items.put(id, item);
//        userItemIndex.computeIfAbsent(userId, k -> new HashMap<>()).put(id, item);
//        return item;
//    }
//
//    @Override
//    public List<Item> getItems(int userId) {
//        return new ArrayList<>(userItemIndex.getOrDefault(userId, Map.of()).values());
//    }
//
//    @Override
//    public Optional<Item> getItemById(int id) {
//        return Optional.ofNullable(items.get(id));
//    }
//
//    @Override
//    public Item update(int userId, int id, Item item) {
//        items.put(id, item);
//        userItemIndex.get(userId).put(id, item);
//        return item;
//    }
//
//    @Override
//    public List<Item> getItemByText(String text) {
//        String finalText = text.toLowerCase();
//        return items.values().stream()
//                .filter(item -> (item.getDescription().toLowerCase().contains(finalText)
//                        || item.getName().toLowerCase().contains(finalText))
//                        && item.getAvailable())
//                .collect(Collectors.toList());
//    }
//}
