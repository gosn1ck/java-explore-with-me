package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponse {
    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}