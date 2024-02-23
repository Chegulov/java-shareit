package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForRequestor;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoForRequestor> getRequests(Long requestorId);

    List<ItemRequestDtoForRequestor> getRequestsByPage(Long userId, Integer start, Integer size);

    ItemRequestDtoForRequestor getRequestById(Long userId, Long requestId);
}
