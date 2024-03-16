package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
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

        itemRepository.save(item);

        booker = User.builder()
                .name("Booker")
                .email("booker@email.com")
                .build();

        userRepository.save(booker);

        booking = Booking.builder()
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        bookingRepository.save(booking);

        pageable = PageRequest.of(0, 10);
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReadAllBookerCurrentBookings() {
        booking.setStartTime(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> returnedBookings = bookingRepository
                .readAllBookerCurrentBookings(booker.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, returnedBookings.size());
        assertEquals(booking.getItem().getId(), returnedBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookings.get(0).getBooker().getId());
        assertEquals(booking.getStartTime(), returnedBookings.get(0).getStartTime());
        assertEquals(booking.getEndTime(), returnedBookings.get(0).getEndTime());
        assertEquals(booking.getStatus(), returnedBookings.get(0).getStatus());
    }

    @Test
    void shouldReadAllBookerPastBookings() {
        booking.setStartTime(LocalDateTime.now().minusHours(2));
        booking.setEndTime(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> returnedBookings = bookingRepository
                .readAllBookerPastBookings(booker.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, returnedBookings.size());
        assertEquals(booking.getItem().getId(), returnedBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookings.get(0).getBooker().getId());
        assertEquals(booking.getStartTime(), returnedBookings.get(0).getStartTime());
        assertEquals(booking.getEndTime(), returnedBookings.get(0).getEndTime());
        assertEquals(booking.getStatus(), returnedBookings.get(0).getStatus());
    }

    @Test
    void shouldReadAllBookerFutureBookings() {
        List<Booking> returnedBookings = bookingRepository
                .readAllBookerFutureBookings(booker.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, returnedBookings.size());
        assertEquals(booking.getItem().getId(), returnedBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookings.get(0).getBooker().getId());
        assertEquals(booking.getStartTime(), returnedBookings.get(0).getStartTime());
        assertEquals(booking.getEndTime(), returnedBookings.get(0).getEndTime());
        assertEquals(booking.getStatus(), returnedBookings.get(0).getStatus());
    }

    @Test
    void shouldReadAllOwnerItemsCurrentBookings() {
        booking.setStartTime(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> returnedBookings = bookingRepository
                .readAllOwnerItemsCurrentBookings(List.of(item.getId()), LocalDateTime.now(), pageable);

        assertEquals(1, returnedBookings.size());
        assertEquals(booking.getItem().getId(), returnedBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookings.get(0).getBooker().getId());
        assertEquals(booking.getStartTime(), returnedBookings.get(0).getStartTime());
        assertEquals(booking.getEndTime(), returnedBookings.get(0).getEndTime());
        assertEquals(booking.getStatus(), returnedBookings.get(0).getStatus());
    }

    @Test
    void shouldReadAllOwnerItemsPastBookings() {
        booking.setStartTime(LocalDateTime.now().minusHours(2));
        booking.setEndTime(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> returnedBookings = bookingRepository
                .readAllOwnerItemsPastBookings(List.of(item.getId()), LocalDateTime.now(), pageable);

        assertEquals(1, returnedBookings.size());
        assertEquals(booking.getItem().getId(), returnedBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookings.get(0).getBooker().getId());
        assertEquals(booking.getStartTime(), returnedBookings.get(0).getStartTime());
        assertEquals(booking.getEndTime(), returnedBookings.get(0).getEndTime());
        assertEquals(booking.getStatus(), returnedBookings.get(0).getStatus());
    }

    @Test
    void shouldReadAllOwnerItemsFutureBookings() {
        List<Booking> returnedBookings = bookingRepository
                .readAllOwnerItemsFutureBookings(List.of(item.getId()), LocalDateTime.now(), pageable);

        assertEquals(1, returnedBookings.size());
        assertEquals(booking.getItem().getId(), returnedBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookings.get(0).getBooker().getId());
        assertEquals(booking.getStartTime(), returnedBookings.get(0).getStartTime());
        assertEquals(booking.getEndTime(), returnedBookings.get(0).getEndTime());
        assertEquals(booking.getStatus(), returnedBookings.get(0).getStatus());
    }
}
