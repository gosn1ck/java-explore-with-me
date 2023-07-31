package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.validator.EventDate;
import ru.practicum.ewm.validator.ValidDateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@EventDate
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    @ValidDateTimeFormat
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
    private StateActions stateAction;
}
