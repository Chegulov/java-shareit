package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ItemAvailabilityException;
import ru.practicum.shareit.exception.WrongAccesException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;

    private UserDto userDto;
    private UserDto user2Dto;
    private ItemDto itemDto;
    private ItemRequestDto itemRequest;
    private ItemDto item2Dto;
    private ItemDto itemDtoToupdate;
    private CommentDto commentDto;
    private BookingDtoInput lastBooking;
    private BookingDtoInput nextBooking;

    @BeforeEach
    void init() {

        userDto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();

        user2Dto = UserDto.builder()
                .id(2L)
                .name("SecondUser")
                .email("secondUser@email.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .requestId(1L)
                .build();

        itemRequest = ItemRequestDto.builder()
                .id(1L)
                .description("ItemRequestDescription")
                .build();

        item2Dto = ItemDto.builder()
                .id(2L)
                .name("Item2")
                .description("Item2Description")
                .available(true)
                .requestId(2L)
                .build();

        lastBooking = BookingDtoInput.builder()
                .bookingId(1L)
                .itemId(1L)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();

        nextBooking = BookingDtoInput.builder()
                .bookingId(2L)
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("commentText")
                .build();
    }

    @Test
    void create_shouldCreateItemWithRequestId() {
        userService.create(userDto);
        UserDto savedRequestor = userService.create(user2Dto);
        ItemRequestDto savedRequest = itemRequestService.create(savedRequestor.getId(), itemRequest);
        itemDto.setRequestId(savedRequest.getId());

        ItemDto createdItemDto = itemService.create(1L, itemDto);

        assertEquals(itemDto.getName(), createdItemDto.getName());
        assertEquals(itemDto.getDescription(), createdItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), createdItemDto.getAvailable());
        assertEquals(itemDto.getRequestId(), createdItemDto.getRequestId());
    }

    @Test
    void create_shouldCreateItemWithoutRequestId() {
        userService.create(userDto);

        ItemDto createdItemDto = itemService.create(1L, itemDto);

        assertEquals(itemDto.getName(), createdItemDto.getName());
        assertEquals(itemDto.getDescription(), createdItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), createdItemDto.getAvailable());
        assertNull(createdItemDto.getRequestId());
    }

    @Test
    void create_shouldThrowDataNotFoundException_WhenUserNotExist() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.create(999L, itemDto));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getItemById_shouldThrowDataNotFoundException_WhenUserNotFound() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.getItemById(999L, 1L));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getItemById_shouldThrowDataNotFoundException_WhenItemNotFound() {
        userService.create(userDto);

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.getItemById(1L,999L));

        assertEquals("Вещи с id=999 нет.", dataNotFoundException.getMessage());
    }

    @Test
    void getItemById_shouldReturnItemWithBookings_WhenUserIsOwner() {
        UserDto savedOwner = userService.create(userDto);
        UserDto savedBooker = userService.create(user2Dto);
        itemService.create(1L, itemDto);
        BookingDtoOutput last = bookingService.create(savedBooker.getId(), lastBooking);
        bookingService.updateStatus(savedOwner.getId(), last.getId(), true);
        BookingDtoOutput next = bookingService.create(savedBooker.getId(), nextBooking);
        bookingService.updateStatus(savedOwner.getId(), next.getId(), true);

        ItemDto returnedItem = itemService.getItemById(1L, 1L);

        assertEquals(itemDto.getId(), returnedItem.getId());
        assertEquals(itemDto.getName(), returnedItem.getName());
        assertEquals(itemDto.getAvailable(), returnedItem.getAvailable());
        assertEquals(1L, returnedItem.getLastBooking().getId());
        assertEquals(2L, returnedItem.getLastBooking().getBookerId());
        assertEquals(2L, returnedItem.getNextBooking().getId());
        assertEquals(2L, returnedItem.getNextBooking().getBookerId());
    }

    @Test
    void getItemById_shouldReturnItemWithoutBookings_WhenUserIsNotOwner() {
        UserDto savedOwner = userService.create(userDto);
        UserDto savedBooker = userService.create(user2Dto);
        itemService.create(1L, itemDto);
        BookingDtoOutput last = bookingService.create(savedBooker.getId(), lastBooking);
        bookingService.updateStatus(savedOwner.getId(), last.getId(), true);
        BookingDtoOutput next = bookingService.create(savedBooker.getId(), nextBooking);
        bookingService.updateStatus(savedOwner.getId(), next.getId(), true);

        ItemDto returnedItem = itemService.getItemById(2L, 1L);

        assertEquals(itemDto.getId(), returnedItem.getId());
        assertEquals(itemDto.getName(), returnedItem.getName());
        assertEquals(itemDto.getAvailable(), returnedItem.getAvailable());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void getItems_shouldThrowDataNotFoundException_WhenUserNotFound() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.getItems(999L, 1, 10));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getItems_shouldReturnItems() {
        UserDto savedOwner = userService.create(userDto);
        UserDto savedBooker = userService.create(user2Dto);
        ItemDto createdItemDto = itemService.create(1L, itemDto);
        BookingDtoOutput last = bookingService.create(savedBooker.getId(), lastBooking);
        bookingService.updateStatus(savedOwner.getId(), last.getId(), true);
        BookingDtoOutput next = bookingService.create(savedBooker.getId(), nextBooking);
        bookingService.updateStatus(savedOwner.getId(), next.getId(), true);

        List<ItemDto> returnedItems = itemService.getItems(1L, 0, 10);

        assertEquals(1, returnedItems.size());

        assertEquals(createdItemDto.getId(), returnedItems.get(0).getId());
        assertEquals(itemDto.getName(), returnedItems.get(0).getName());
        assertEquals(itemDto.getDescription(), returnedItems.get(0).getDescription());
        assertEquals(itemDto.getAvailable(), returnedItems.get(0).getAvailable());
        assertEquals(1L, returnedItems.get(0).getLastBooking().getId());
        assertEquals(2L, returnedItems.get(0).getLastBooking().getBookerId());
        assertEquals(2L, returnedItems.get(0).getNextBooking().getId());
        assertEquals(2L, returnedItems.get(0).getNextBooking().getBookerId());
    }

    @Test
    void update_shouldThrowDataNotFoundException_WhenUserNotFound() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.update(999L, 1L, itemDtoToupdate));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void update_shouldThrowDataNotFoundException_WhenItemNotFound() {
        userService.create(userDto);

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.update(1L, 999L, itemDtoToupdate));

        assertEquals("Вещи с id=999 нет.", dataNotFoundException.getMessage());
    }

    @Test
    void update_shouldThrowWrongAccesException_WhenItemNotFound() {
        userService.create(userDto);
        userService.create(user2Dto);
        itemService.create(1L, itemDto);

        WrongAccesException wrongAccesException = assertThrows(WrongAccesException.class,
                () -> itemService.update(2L, 1L, itemDtoToupdate));

        assertEquals("У вещи с id=1 другой владелец.", wrongAccesException.getMessage());
    }

    @Test
    void update_shouldUpdateItem() {
        userService.create(userDto);
        userService.create(user2Dto);
        itemService.create(1L, itemDto);
        itemDtoToupdate = ItemDto.builder()
                .id(1L)
                .name("UpdatedItem")
                .description("UpdatedItemDescription")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.update(1L, 1L, itemDtoToupdate);

        assertEquals(itemDtoToupdate.getId(), updatedItem.getId());
        assertEquals(itemDtoToupdate.getName(), updatedItem.getName());
        assertEquals(itemDtoToupdate.getDescription(), updatedItem.getDescription());
        assertEquals(itemDtoToupdate.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void getItemByText_shouldGetItem() {
        userService.create(userDto);
        userService.create(user2Dto);
        ItemDto createdItemDto = itemService.create(1L, itemDto);
        ItemDto createdItem2Dto = itemService.create(1L, item2Dto);

        List<ItemDto> returnedItems = itemService.getItemByText(1L, "descr", 0, 10);

        assertEquals(2, returnedItems.size());
        assertEquals(createdItemDto.getId(), returnedItems.get(0).getId());
        assertEquals(createdItemDto.getName(), returnedItems.get(0).getName());
        assertEquals(createdItem2Dto.getId(), returnedItems.get(1).getId());
        assertEquals(createdItem2Dto.getName(), returnedItems.get(1).getName());
    }

    @Test
    void getItemByText_shouldThrowDataNotFoundException_WhenUserNotFound() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.getItemByText(999L, "descr", 0, 10));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getItemByText_shouldGetEmptyList_WhenTextIsBlank() {
        userService.create(userDto);

        List<ItemDto> returnedItems = itemService.getItemByText(1L, " ", 0, 10);

        assertEquals(0, returnedItems.size());
    }

    @Test
    void createComment_shouldCreateComment() {
        UserDto savedOwner = userService.create(userDto);
        UserDto savedBooker = userService.create(user2Dto);
        itemService.create(1L, itemDto);
        BookingDtoOutput createdBooking = bookingService.create( savedBooker.getId(), lastBooking);
        bookingService.updateStatus(savedOwner.getId(), createdBooking.getId(), true);

        CommentDto returnedComment = itemService.createComment(2L, 1L, commentDto);

        assertEquals(savedBooker.getName(), returnedComment.getAuthorName());
        assertEquals(1L, returnedComment.getId());
        assertEquals(commentDto.getText(), returnedComment.getText());
    }

    @Test
    void createComment__shouldThrowDataNotFoundException_WhenUserNotFound() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> itemService.createComment(999L, 1L, commentDto));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void createComment__shouldThrowItemAvailabilityException_WhenItemNotBookered() {
        userService.create(userDto);
        userService.create(user2Dto);
        itemService.create(1L, itemDto);

        ItemAvailabilityException itemAvailabilityException = assertThrows(ItemAvailabilityException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertEquals("Пользователь с id=1 не бронировал вещь с id=1", itemAvailabilityException.getMessage());
    }
}
