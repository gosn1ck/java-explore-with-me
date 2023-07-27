package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.validator.EventDate;
import ru.practicum.ewm.validator.ValidDateTimeFormat;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EventDate
public class NewEventDto {
    @NotBlank(message = "annotation field should not be empty")
    @Size(min = 10, max = 2000, message = "annotation field should be min 10, max 2000")
    private String annotation;

    @NotNull(message = "category field should not be null")
    private Long category;

    @NotBlank(message = "description field should not be empty")
    @Size(min = 20, max = 7000, message = "description field should be min 20, max 7000")
    private String description;

    @ValidDateTimeFormat
    private String eventDate;

    @NotNull(message = "location field should not be null")
    private Location location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @NotBlank(message = "title field should not be empty")
    @Size(min = 3, max = 120, message = "title field should be min 3, max 120")
    private String title;

}
