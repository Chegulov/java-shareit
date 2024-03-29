package ru.practicum.shareit.validateGroups;

import ru.practicum.shareit.annotation.StartEndOfBookingValidation;
import ru.practicum.shareit.booking.BookingDtoInput;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndOfBookingValidator implements ConstraintValidator<StartEndOfBookingValidation, BookingDtoInput> {
    @Override
    public boolean isValid(BookingDtoInput bookingDtoInput, ConstraintValidatorContext constraintValidatorContext) {
        boolean isEndNull = (bookingDtoInput.getEnd() == null);

        boolean isStartNull = (bookingDtoInput.getStart() == null);

        if (isEndNull || isStartNull) {
            return false;
        }

        boolean isEndFutureOrPresent = bookingDtoInput.getEnd().equals(LocalDateTime.now())
                || bookingDtoInput.getEnd().isAfter(LocalDateTime.now());

        boolean isStartFutureOrPresent = bookingDtoInput.getStart().equals(LocalDateTime.now())
                || bookingDtoInput.getStart().isAfter(LocalDateTime.now());

        boolean isEndAfterStart = bookingDtoInput.getEnd().isAfter(bookingDtoInput.getStart());

        boolean isNotEndEqualStart = !bookingDtoInput.getEnd().equals(bookingDtoInput.getStart());

        return isEndFutureOrPresent
                && isStartFutureOrPresent
                && isEndAfterStart
                && isNotEndEqualStart;
    }
}
