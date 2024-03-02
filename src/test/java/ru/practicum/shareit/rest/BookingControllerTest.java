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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.constant.CustomHeaders.USER_ID;
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private BookingDtoInput bookingDtoInput;
    private BookingDtoOutput bookingDtoOutput;

    @BeforeEach
    void init() {
        bookingDtoInput = BookingDtoInput.builder()
                .bookingId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(1L)
                .build();

        bookingDtoOutput = BookingDtoOutput.builder()
                .id(1L)
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .build();
    }

    @Test
    @SneakyThrows
    void create_Status200_WhenAllOk() {
        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any())).thenReturn(bookingDtoOutput);

        String result = mockMvc.perform(post("/bookings")
                .header(USER_ID, 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).create(Mockito.anyLong(), Mockito.any());

        assertEquals(objectMapper.writeValueAsString(bookingDtoOutput), result);
    }

    @Test
    @SneakyThrows
    void create_Status400_whenEndNull() {
        bookingDtoInput.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void create_Status400_whenStartNull() {
        bookingDtoInput.setStart(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void create_Status400_whenEndBeforeStart() {
        bookingDtoInput.setEnd(bookingDtoInput.getStart().minusHours(10));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void create_Status400_whenStartInPast() {
        bookingDtoInput.setStart(LocalDateTime.now().minusHours(15));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void create_Status400_whenStartEqualsEnd() {
        bookingDtoInput.setStart(bookingDtoInput.getEnd());

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void updateStatus_Status200() {
        Mockito.when(bookingService.updateStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDtoOutput);

        String result = mockMvc.perform(patch("/bookings/{id}", 1L)
                        .header(USER_ID, 1L)
                        .param("approved", "true")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoInput)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).updateStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());

        assertEquals(objectMapper.writeValueAsString(bookingDtoOutput), result);
    }

    @Test
    @SneakyThrows
    void getBooking_Status200() {
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDtoOutput);

        mockMvc.perform(get("/bookings/{id}", 1L)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        Mockito.verify(bookingService).getBooking(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @SneakyThrows
    void getAllBookerBookings_Status200() {
        Mockito.when(bookingService
                .getAllBookerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoOutput));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoOutput))));

        Mockito.verify(bookingService)
                .getAllBookerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @SneakyThrows
    void getAllOwnerItemBookings_Status200() {
        Mockito.when(bookingService
                        .getAllOwnerItemBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoOutput));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoOutput))));

        Mockito.verify(bookingService)
                .getAllOwnerItemBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }
}
