package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ItemAvailabilityException;
import ru.practicum.shareit.exception.WrongAccesException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = itemMapper.createItemFromDto(userId, itemDto);
        item.setOwner(user);
        return itemMapper.getItemDto(itemRepository.save(item), null, null, null);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    Booking lastBooking = findLastBooking(item.getId());
                    Booking nextBooking = findNextBooking(item.getId());
                    return itemMapper.getItemDto(item,
                            bookingMapper.createBookingItemDto(lastBooking),
                            bookingMapper.createBookingItemDto(nextBooking),
                            findComments(item.getId()));
                })
                .sorted(Comparator.comparingLong(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = itemRepository.findById(id)
                        .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет."));
        Booking lastBooking = findLastBooking(id);
        Booking nextBooking = findNextBooking(id);

        if (userId == item.getOwner().getId()) {
            return itemMapper.getItemDto(item,
                    bookingMapper.createBookingItemDto(lastBooking),
                    bookingMapper.createBookingItemDto(nextBooking),
                    findComments(id));
        }

        return itemMapper.getItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет.")),
                null,
                null,
                findComments(id));
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + id + " нет."));
        if (item.getOwner().getId() != userId) {
            throw new WrongAccesException("У вещи с id=" + id + " другой владелец.");
        }
        item = itemMapper.updateItemFromDto(item, itemDto);

        return itemMapper.getItemDto(itemRepository.save(item),
                bookingMapper.createBookingItemDto(findLastBooking(id)),
                bookingMapper.createBookingItemDto(findNextBooking(id)),
                findComments(id));
    }

    @Override
    public List<ItemDto> getItemByText(Long userId, String text) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));
        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.findItemByText(text).stream()
                .filter(Item::getAvailable)
                .map(item -> itemMapper.getItemDto(item,
                            null,
                            null,
                            findComments(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + userId + " не найден."));

        List<Booking> itemBookings = bookingRepository
                .findAllByItem_IdAndBooker_IdAndStatus(itemId, userId, BookingStatus.APPROVED)
                .stream()
                .filter(booking -> booking.getEndTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (itemBookings.size() == 0) {
            throw new ItemAvailabilityException("Пользователь с id=" + userId + " не бронировал вещь с id=" + itemId);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Вещи с id=" + itemId + " нет."));

        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.createCommentFromDto(commentDto, item, user);
        return commentMapper.getCommentDto(commentRepository.save(comment));
    }

    private Booking findLastBooking(Long itemId) {
        List<Booking> itemBookings = bookingRepository.findAllByItemId(itemId);

        return itemBookings.stream()
                .filter(booking -> booking.getStartTime().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .max(Comparator.comparing(Booking::getEndTime))
                .orElse(null);
    }

    private Booking findNextBooking(Long itemId) {
        List<Booking> itemBookings = bookingRepository.findAllByItemId(itemId);

        return itemBookings.stream()
                .filter(booking -> booking.getStartTime().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStartTime))
                .orElse(null);
    }

    private List<CommentDto> findComments(long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::getCommentDto)
                .collect(Collectors.toList());
    }
}
