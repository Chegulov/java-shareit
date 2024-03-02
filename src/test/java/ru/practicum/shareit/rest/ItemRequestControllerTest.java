package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForRequestor;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoForRequestor itemRequestDtoForRequestor;

    @BeforeEach
    void init() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("ItemRequestDescription")
                .build();

        itemRequestDtoForRequestor = ItemRequestDtoForRequestor.builder()
                .id(1L)
                .description("ItemRequestDescription")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    @SneakyThrows
    void create_Status200AndReturnedItemRequest_WhenAllOk() {
        Mockito.when(itemRequestService.create(1L, itemRequestDto))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemRequestService).create(Mockito.anyLong(), Mockito.any());

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @Test
    @SneakyThrows
    void create_Status400_WhenEmptyDescription() {
        itemRequestDto.setDescription(null);

        Mockito.when(itemRequestService.create(1L, itemRequestDto))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemRequestService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void getRequestById_Status200() {
        Mockito.when(itemRequestService.getRequestById(1L, 1L)).thenReturn(itemRequestDtoForRequestor);

        mockMvc.perform(get("/requests/{id}", 1L)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("ItemRequestDescription"));

        Mockito.verify(itemRequestService).getRequestById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void getRequestsByPage_Status200() {
        Mockito.when(itemRequestService.getRequestsByPage(1L, 0, 10))
                .thenReturn(List.of(itemRequestDtoForRequestor));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(List.of(itemRequestDtoForRequestor))));

        Mockito.verify(itemRequestService).getRequestsByPage(1L, 0, 10);
    }

    @Test
    @SneakyThrows
    void getRequests_Status200() {
        Mockito.when(itemRequestService.getRequests(1L))
                .thenReturn(List.of(itemRequestDtoForRequestor));

        mockMvc.perform(get("/requests")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(List.of(itemRequestDtoForRequestor))));

        Mockito.verify(itemRequestService).getRequests(1L);
    }
}
