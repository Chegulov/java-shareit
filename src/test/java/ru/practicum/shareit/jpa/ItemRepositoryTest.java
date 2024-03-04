package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private Item item;
    private Item item2;
    private Pageable pageable;

    @BeforeEach
    void init() {
        owner = User.builder()
                .name("User")
                .email("user@email.com")
                .build();

        userRepository.save(owner);

        item = Item.builder()
                .name("Item")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build();

        item2 = Item.builder()
                .name("Item2")
                .description("Item2 Descr")
                .available(true)
                .owner(owner)
                .build();

        itemRepository.save(item);
        itemRepository.save(item2);

        pageable = PageRequest.of(0, 10);
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSearchByText() {
        List<Item> items = itemRepository.findItemByText("description", pageable);

        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
    }
}
