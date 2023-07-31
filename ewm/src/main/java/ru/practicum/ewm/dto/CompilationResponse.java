package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationResponse {
    private Set<EventShortDto> events;
    private Long id;
    private String title;
    private Boolean pinned;
}
