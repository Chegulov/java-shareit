package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {

    BookingDtoOutput create(Long bookerId, BookingDtoInput bookingDtoInput);

    BookingDtoOutput updateStatus(Long ownerId, Long bookingId, Boolean isApproved);

    BookingDtoOutput getBooking(Long userId, Long bookingId);

    List<BookingDtoOutput> getAllBookerBookings(Long userId, String state);

    List<BookingDtoOutput> getAllOwnerItemBookings(Long ownerId, String state);
}
