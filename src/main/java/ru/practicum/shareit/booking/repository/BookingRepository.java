package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.startTime <= ?2 " +
            "AND b.endTime >= ?2 " +
            "ORDER BY b.startTime DESC")
    List<Booking> readAllBookerCurrentBookings(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.startTime <= ?2 " +
            "AND b.endTime <= ?2 " +
            "ORDER BY b.startTime DESC")
    List<Booking> readAllBookerPastBookings(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.startTime >= ?2 " +
            "AND b.endTime >= ?2 " +
            "ORDER BY b.startTime DESC")
    List<Booking> readAllBookerFutureBookings(long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartTimeDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByBooker_IdOrderByStartTimeDesc(long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.startTime <= ?2 " +
            "AND b.endTime >= ?2 " +
            "ORDER BY b.startTime DESC")
    List<Booking> readAllOwnerItemsCurrentBookings(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.startTime <= ?2 " +
            "AND b.endTime <= ?2 " +
            "ORDER BY b.startTime DESC")
    List<Booking> readAllOwnerItemsPastBookings(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.startTime >= ?2 " +
            "AND b.endTime >= ?2 " +
            "ORDER BY b.startTime DESC")
    List<Booking> readAllOwnerItemsFutureBookings(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItem_IdInOrderByStartTimeDesc(List<Long> itemIds);

    List<Booking> findAllByItem_IdInAndStatusInOrderByStartTimeDesc(List<Long> itemIds, List<BookingStatus> status);

    List<Booking> findAllByItem_IdAndBooker_IdAndStatus(Long itemId, Long bookerId, BookingStatus status);

    List<Booking> findAllByItemId(Long itemId);
}
