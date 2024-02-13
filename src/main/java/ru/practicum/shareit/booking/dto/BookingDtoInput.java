package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.StartEndOfBookingValidation;

import java.time.LocalDateTime;

@Data
@Builder
@StartEndOfBookingValidation
public class BookingDtoInput {
    private Long bookingId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
