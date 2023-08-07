package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryResponse category;
    private Integer confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortResponse initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
