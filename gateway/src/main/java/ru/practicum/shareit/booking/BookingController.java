package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.WrongStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getAllBookerBookings(@RequestHeader(USER_ID) long userId,
								  @RequestParam(name = "state", defaultValue = "all") String state,
								  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
								  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState.from(state)
				.orElseThrow(() -> new WrongStateException("Unknown state: " + state));
		log.info("Get Booker bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getAllBookerBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(USER_ID) long userId,
										 @RequestBody @Valid BookingDtoInput bookingDtoInput) {
		log.info("Creating booking {}, userId={}", bookingDtoInput, userId);
		return bookingClient.create(userId, bookingDtoInput);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllOwnerItemBookings(@RequestHeader(USER_ID) Long ownerId,
								  @RequestParam(defaultValue = "ALL") String state,
								  @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
								  @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
		BookingState.from(state)
				.orElseThrow(() -> new WrongStateException("Unknown state: " + state));
		log.info("Get All Owner tem bookings with state {}, userId={}, from={}, size={}", state, ownerId, from, size);
		return bookingClient.getAllOwnerItemBookings(ownerId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateStatus(@RequestHeader(USER_ID) Long ownerId,
										 @PathVariable Long bookingId,
										 @RequestParam("approved") Boolean isApproved) {
		log.info("Update status ownerId={}, bookingId={}, isApproved={}", ownerId, bookingId, isApproved);
		return bookingClient.updateStatus(ownerId, bookingId, isApproved);
	}
}
