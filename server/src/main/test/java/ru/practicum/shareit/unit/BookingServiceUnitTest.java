package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private BookingMapper bookingMapper;

    private Booking booking;
    private Booking updatedBooking;
    private BookingDtoInput bookingDtoInput;
    private BookingDtoInput previousBooking;
    private BookingDtoInput futureBooking;
    private Booking booking1;
    private Booking booking2;
    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private Item item3;
    private Pageable pageable;

    @BeforeEach
    void init() {
        bookingMapper = new BookingMapper(new ItemMapper(), new UserMapper());

        ReflectionTestUtils.setField(bookingService, "bookingMapper", bookingMapper);

        bookingDtoInput = BookingDtoInput.builder()
                .bookingId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .itemId(1L)
                .build();

        previousBooking = BookingDtoInput.builder()
                .bookingId(1L)
                .start(LocalDateTime.of(2024, 3, 1, 10, 0))
                .end(LocalDateTime.of(2024, 3, 1, 11, 0))
                .itemId(1L)
                .build();

        futureBooking = BookingDtoInput.builder()
                .bookingId(1L)
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(3))
                .itemId(1L)
                .build();

        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("AnotherUser")
                .email("AnotherUser@email.com")
                .build();

        item = Item.builder()
                .id(1L)
                .description("ItemDescription")
                .available(true)
                .owner(user)
                .build();

        item2 = Item.builder()
                .id(2L)
                .description("SecondItemDescription")
                .available(true)
                .owner(user)
                .build();

        item3 = Item.builder()
                .id(3L)
                .description("ThirdItemDescription")
                .available(true)
                .owner(user)
                .build();

        booking = bookingMapper.createBookingFromDto(bookingDtoInput, item, user2, BookingStatus.WAITING);
        booking1 = bookingMapper.createBookingFromDto(previousBooking, item, user2, BookingStatus.WAITING);
        booking2 = bookingMapper.createBookingFromDto(futureBooking, item, user2, BookingStatus.APPROVED);
        updatedBooking = bookingMapper.createBookingFromDto(bookingDtoInput, item, user2, BookingStatus.APPROVED);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void create_shouldCreateBooking() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDtoOutput createdBooking = bookingService.create(2L, bookingDtoInput);

        Mockito.verify(bookingRepository).save(Mockito.any());

        assertEquals(bookingDtoInput.getBookingId(), createdBooking.getId());
        assertEquals(bookingDtoInput.getStart(), createdBooking.getStart());
        assertEquals(bookingDtoInput.getEnd(), createdBooking.getEnd());
        assertEquals(item.getId(), createdBooking.getItem().getId());
        assertEquals(item.getName(), createdBooking.getItem().getName());
        assertEquals(user2.getId(), createdBooking.getBooker().getId());
        assertEquals(user2.getName(), createdBooking.getBooker().getName());
    }

    @Test
    void create_shouldThrowDataNotFoundException_WhenUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.create(999L, bookingDtoInput));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void create_shouldThrowDataNotFoundException_WhenItemNotExist() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.create(2L, bookingDtoInput));

        assertEquals("Вещи с id=1 нет.", dataNotFoundException.getMessage());
    }

    @Test
    void create_shouldThrowDataNotFoundException_WhenBookerIsOwner() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.create(1L, bookingDtoInput));

        assertEquals("невозможно забронировать свою же вещь.", dataNotFoundException.getMessage());
    }

    @Test
    void create_shouldThrowItemAvailabilityException_WhenItemNotAvailable() {
        item.setAvailable(false);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemAvailabilityException itemAvailabilityException = assertThrows(ItemAvailabilityException.class,
                () -> bookingService.create(2L, bookingDtoInput));

        assertEquals("вещь недоступна", itemAvailabilityException.getMessage());
    }

    @Test
    void updateStatus_shouldThrowDataNotFoundException_WhenUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.updateStatus(999L, 1L, true));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void updateStatus_shouldThrowDataNotFoundException_WhenBookingNotExist() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.updateStatus(2L, 999L, true));

        assertEquals("Бронирование с id=999 не найдено.", dataNotFoundException.getMessage());
    }

    @Test
    void updateStatus_shouldThrowWrongAccesException_WhenNotOwner() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        WrongAccesException wrongAccesException = assertThrows(WrongAccesException.class,
                () -> bookingService.updateStatus(2L, 1L, true));

        assertEquals("Пользователь с id=2 не может менять статус бронирования с id=1", wrongAccesException.getMessage());
    }

    @Test
    void updateStatus_shouldThrowWrongStatusException_WhenStatusNotWaiting() {
        booking.setStatus(BookingStatus.REJECTED);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        WrongStatusException wrongStatusException = assertThrows(WrongStatusException.class,
                () -> bookingService.updateStatus(1L, 1L, true));

        assertEquals("Невозможно изменить статус вещи. Текущий статус REJECTED", wrongStatusException.getMessage());
    }

    @Test
    void updateStatus_shouldUpdateStatus() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(updatedBooking);

        BookingDtoOutput returnedBooking = bookingService.updateStatus(1L, 1L, true);

        Mockito.verify(bookingRepository).save(Mockito.any());

        assertEquals(BookingStatus.APPROVED, returnedBooking.getStatus());
        assertEquals(booking.getId(), returnedBooking.getId());
    }

    @Test
    void getBooking_shouldThrowDataNotFoundException_WhenUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(999L, 1L));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getBooking_shouldThrowDataNotFoundException_WhenBookingNotFindByUser() {
        booking.setBooker(user);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(2L, 1L));

        assertEquals("Бронирование с id=1 не найдено для пользователя с id=2", dataNotFoundException.getMessage());
    }

    @Test
    void getBooking_shouldThrowDataNotFoundException_WhenBookingNotExist() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(2L, 999L));

        assertEquals("Бронирование с id=999 не найдено.", dataNotFoundException.getMessage());
    }

    @Test
    void getBooking_shouldGetBooking() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDtoOutput returnedBooking =  bookingService.getBooking(2L, 1L);

        assertEquals(booking.getId(), returnedBooking.getId());
        assertEquals(booking.getItem().getId(), returnedBooking.getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBooking.getBooker().getId());
    }

    @Test
    void getAllBookerBookings_shouldThrowDataNotFoundException_WhenUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.getAllBookerBookings(999L, "ALL", 0, 10));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getAllBookerBookings_shouldThrowWrongStateException_WhenWrongState() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        WrongStateException wrongStateException = assertThrows(WrongStateException.class,
                () -> bookingService.getAllBookerBookings(1L, " ", 0, 10));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", wrongStateException.getMessage());
    }

    @Test
    void getAllBookerBookings_WhenStateALL() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(2L, pageable))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllBookerBookings(2L, "ALL", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllBookerBookings_WhenStateCURRENT() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.readAllBookerCurrentBookings(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllBookerBookings(2L, "CURRENT", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllBookerBookings_WhenStatePAST() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.readAllBookerPastBookings(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllBookerBookings(2L, "PAST", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllBookerBookings_WhenStateFUTURE() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.readAllBookerFutureBookings(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllBookerBookings(2L, "FUTURE", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllBookerBookings_WhenStateWAITING_or_REJECTED() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartTimeDesc(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllBookerBookings(2L, "WAITING", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllOwnerItemBookings_shouldThrowDataNotFoundException_WhenUserNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookingService.getAllOwnerItemBookings(999L, "ALL", 0, 10));

        assertEquals("Пользователь с id=999 не найден.", dataNotFoundException.getMessage());
    }

    @Test
    void getAllOwnerItemBookings_shouldThrowWrongStateException_WhenWrongState() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item, item2, item3));

        WrongStateException wrongStateException = assertThrows(WrongStateException.class,
                () -> bookingService.getAllOwnerItemBookings(1L, " ", 0, 10));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", wrongStateException.getMessage());
    }

    @Test
    void getAllOwnerItemBookings_WhenStateALL() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item, item2, item3));
        Mockito.when(bookingRepository.findAllByItem_IdInOrderByStartTimeDesc(List.of(1L, 2L, 3L), pageable))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllOwnerItemBookings(2L, "ALL", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllOwnerItemBookings_WhenStateCURRENT() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item, item2, item3));
        Mockito.when(bookingRepository.readAllOwnerItemsCurrentBookings(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllOwnerItemBookings(2L, "CURRENT", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllOwnerItemBookings_WhenStatePAST() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item, item2, item3));
        Mockito.when(bookingRepository.readAllOwnerItemsPastBookings(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllOwnerItemBookings(2L, "PAST", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllOwnerItemBookings_WhenStateFUTURE() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item, item2, item3));
        Mockito.when(bookingRepository.readAllOwnerItemsFutureBookings(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllOwnerItemBookings(2L, "FUTURE", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }

    @Test
    void getAllOwnerItemBookings_WhenStateWAITING_or_REJECTED() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item, item2, item3));
        Mockito.when(bookingRepository.findAllByItem_IdInAndStatusInOrderByStartTimeDesc(List.of(1L, 2L, 3L),
                        List.of(BookingStatus.WAITING), pageable))
                .thenReturn(List.of(booking, booking1, booking2));

        List<BookingDtoOutput> returnedBookings = bookingService.getAllOwnerItemBookings(2L, "WAITING", 0, 10);

        assertEquals(3, returnedBookings.size());
        assertEquals(booking.getId(), returnedBookings.get(0).getId());
        assertEquals(booking1.getId(), returnedBookings.get(1).getId());
        assertEquals(booking2.getId(), returnedBookings.get(2).getId());
    }
}
