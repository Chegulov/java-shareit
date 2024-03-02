package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForRequestor;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validateGroups.Create;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) Long userId,
                                 @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoForRequestor> getRequests(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoForRequestor> getRequestsByPage(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer start,
                                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemRequestService.getRequestsByPage(userId, start, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoForRequestor getRequestById(@RequestHeader(USER_ID) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
