package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validateGroups.Create;

import javax.validation.constraints.Min;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByPage(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer start,
                                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemRequestClient.getRequestsByPage(userId, start, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID) Long userId, @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
