package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private ItemMapper itemMapper;
    private CommentMapper commentMapper;
    private BookingMapper bookingMapper;
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private ItemDto itemDtoToupdate;
    private CommentDto comment;
    private Booking lastBooking;
    private Booking nextBooking;
    private BookingItemDto lastBookingItemDto;
    private BookingItemDto nextBookingItemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void init() {
        itemMapper = new ItemMapper();
        commentMapper = new CommentMapper();
        userMapper = new UserMapper();
        bookingMapper = new BookingMapper(itemMapper, userMapper);

        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "bookingMapper", bookingMapper);

        userDto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();
        user = userMapper.createUserFromDto(userDto);

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .requestId(1L)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequestDescription")
                .created(LocalDateTime.now())
                .build();

        item = itemMapper.createItemFromDto(itemDto, itemRequest);

        lastBooking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();

        nextBooking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .build();

        lastBookingItemDto = bookingMapper.createBookingItemDto(lastBooking);
        nextBookingItemDto = bookingMapper.createBookingItemDto(nextBooking);


    }
    @Test
    void shouldCreateItemWithRequestId() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemDto createdItemDto = itemService.create(1L, itemDto);

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(itemRequestRepository).findById(1L);
        Mockito.verify(itemRepository).save(Mockito.any());

        assertEquals(itemDto.getName(), createdItemDto.getName());
        assertEquals(itemDto.getDescription(), createdItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), createdItemDto.getAvailable());
        assertEquals(itemDto.getRequestId(), createdItemDto.getRequestId());
    }
}
