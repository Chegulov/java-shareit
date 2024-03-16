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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDto itemDtoToUpdate;
    private CommentDto commentDto;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    @SneakyThrows
    void create_Status200AndReturnedItem_WhenAllOk() {
        Mockito.when(itemService.create(1L, itemDto))
                .thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).create(Mockito.anyLong(), Mockito.any());

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @Test
    @SneakyThrows
    void create_Status400_WhenWrongName() {
        itemDto.setName(null);

        Mockito.when(itemService.create(1L, itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void create_Status400_WhenWrongDescription() {
        itemDto.setDescription(null);

        Mockito.when(itemService.create(1L, itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void create_Status400_WhenAvailableNull() {
        itemDto.setAvailable(null);

        Mockito.when(itemService.create(1L, itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void getItemById_Status200() {
        Mockito.when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", 1L)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("ItemDescription"));

        Mockito.verify(itemService).getItemById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void getItems_Status200() {
        Mockito.when(itemService.getItems(1L, 0, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(List.of(itemDto))));

        Mockito.verify(itemService).getItems(1L, 0, 10);
    }

    @Test
    @SneakyThrows
    void getItemByText_Status200() {
        Mockito.when(itemService.getItemByText(1L, "descr", 0, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(USER_ID, 1L)
                        .param("text", "descr"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(List.of(itemDto))));

        Mockito.verify(itemService).getItemByText(1L, "descr", 0, 10);
    }

    @Test
    @SneakyThrows
    void update_Status200() {
        itemDtoToUpdate = ItemDto.builder()
                .id(1L)
                .name("UpdatedItem")
                .description("UpdatedItemDescription")
                .available(false)
                .build();

        Mockito.when(itemService.update(1L, 1L, itemDtoToUpdate)).thenReturn(itemDtoToUpdate);

        String result = mockMvc.perform(patch("/items/{id}", 1L)
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());

        assertEquals(objectMapper.writeValueAsString(itemDtoToUpdate), result);
    }

    @Test
    @SneakyThrows
    void createComment_Status200_WhenAllOk() {
        commentDto = CommentDto.builder()
                .id(1L)
                .text("Comment")
                .created(LocalDateTime.now())
                .build();

        Mockito.when(itemService.createComment(1L, 1L, commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                .header(USER_ID, 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).createComment(1L, 1L, commentDto);

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }

    @Test
    @SneakyThrows
    void createComment_Status400_WhenTextNull() {
        commentDto = CommentDto.builder()
                .id(1L)
                .text(null)
                .created(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).createComment(1L, 1L, commentDto);
    }
}
