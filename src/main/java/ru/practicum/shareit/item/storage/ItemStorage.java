package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item as i " +
            "WHERE upper(i.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) LIKE upper(concat('%', ?1, '%'))")
    List<Item> findItemByText(String text);
}
