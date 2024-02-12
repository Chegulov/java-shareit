package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput create(@RequestHeader(USER_ID) Long bookerId,
                                   @Valid @RequestBody BookingDtoInput bookingDtoInput) {

        return bookingService.create(bookerId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput updateStatus(@RequestHeader(USER_ID) Long ownerId,
                                        @PathVariable Long bookingId,
                                        @RequestParam("approved") Boolean isApproved) {
        return bookingService.updateStatus(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBooking(@RequestHeader(USER_ID) Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutput> getAllBookerBookings(@RequestHeader(USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookerBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllOwnerItemBookings(@RequestHeader(USER_ID) Long ownerId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerItemBookings(ownerId, state);
    }
}
