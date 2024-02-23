package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDtoOutput create(Long bookerId, BookingDtoInput bookingDtoInput) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + bookerId + " не найден."));
        Item item = itemRepository.findById(bookingDtoInput.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + bookingDtoInput.getItemId() + " нет."));

        if (bookerId.equals(item.getOwner().getId())) {
            throw new DataNotFoundException("невозможно забронировать свою же вещь.");
        }

        if (!item.getAvailable()) {
            throw new ItemAvailabilityException("вещь недоступна");
        }
        Booking booking = bookingMapper.createBookingFromDto(bookingDtoInput, item, booker, BookingStatus.WAITING);
        return bookingMapper.createDtoOutput(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput updateStatus(Long ownerId, Long bookingId, Boolean isApproved) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + ownerId + " не найден."));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Бронирование с id=" + bookingId + " не найдено."));

        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new WrongAccesException("Пользователь с id=" + ownerId
                    + " не может менять статус бронирования с id=" + bookingId);
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new WrongStatusException("Невозможно изменить статус вещи. Текущий статус " + booking.getStatus());
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.createDtoOutput(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput getBooking(Long userId, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Бронирование с id=" + bookingId + " не найдено."));

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataNotFoundException("Бронирование с id=" + bookingId
                    + " не найдено для пользователя с id=" + userId);
        }
        return bookingMapper.createDtoOutput(booking);
    }

    @Override
    public List<BookingDtoOutput> getAllBookerBookings(Long userId, String state, Integer start, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Pageable pageable = PageRequest.of(start/size, size);

        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .readAllBookerCurrentBookings(userId, LocalDateTime.now(), pageable));
            case "PAST":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .readAllBookerPastBookings(userId, LocalDateTime.now(), pageable));
            case "FUTURE":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .readAllBookerFutureBookings(userId, LocalDateTime.now(), pageable));
            case "WAITING":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .findAllByBooker_IdAndStatusOrderByStartTimeDesc(userId, BookingStatus.WAITING, pageable));
            case "REJECTED":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .findAllByBooker_IdAndStatusOrderByStartTimeDesc(userId, BookingStatus.REJECTED, pageable));
            case "ALL":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .findAllByBooker_IdOrderByStartTimeDesc(userId, pageable));
            default:
                throw new WrongStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDtoOutput> getAllOwnerItemBookings(Long ownerId, String state, Integer start, Integer size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + ownerId + " не найден."));
        Pageable pageable = PageRequest.of(start/size, size);
        List<Long> itemIds = itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .readAllOwnerItemsCurrentBookings(itemIds, LocalDateTime.now(), pageable));
            case "PAST":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .readAllOwnerItemsPastBookings(itemIds, LocalDateTime.now(), pageable));
            case "FUTURE":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .readAllOwnerItemsFutureBookings(itemIds, LocalDateTime.now(), pageable));
            case "WAITING":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .findAllByItem_IdInAndStatusInOrderByStartTimeDesc(itemIds, List.of(BookingStatus.WAITING), pageable));
            case "REJECTED":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .findAllByItem_IdInAndStatusInOrderByStartTimeDesc(itemIds, List.of(BookingStatus.REJECTED), pageable));
            case "ALL":
                return bookingMapper.createDtoOutputList(bookingRepository
                        .findAllByItem_IdInOrderByStartTimeDesc(itemIds, pageable));
            default:
                throw new WrongStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
