package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validateGroups.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotNull(groups = Create.class)
    private String description;
    private LocalDateTime created;
}
