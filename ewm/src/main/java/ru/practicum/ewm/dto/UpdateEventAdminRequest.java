package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "annotation field should be min 20, max 2000")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "description field should be min 20, max 7000")
    private String description;

//    @ValidDateTimeFormat
    private String eventDate;

    private Location location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "title field should be min 3, max 120")
    private String title;

    private StateActions stateAction;
}