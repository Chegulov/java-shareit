package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public Booking createBookingFromDto(BookingDtoInput bookingDtoInput, Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .id(bookingDtoInput.getBookingId())
                .startTime(bookingDtoInput.getStart())
                .endTime(bookingDtoInput.getEnd())
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }

    public BookingDtoOutput createDtoOutput(Booking booking) {
        return BookingDtoOutput.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .item(itemMapper.getItemDto(booking.getItem(), null, null, null))
                .booker(userMapper.getUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public BookingItemDto createBookingItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public List<BookingDtoOutput> createDtoOutputList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::createDtoOutput)
                .collect(Collectors.toList());
    }
}
