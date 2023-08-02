package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponse {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}
