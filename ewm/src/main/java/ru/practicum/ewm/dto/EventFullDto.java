package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryResponse category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private Long id;
    private UserShortResponse initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String title;
    private Integer views;
}
