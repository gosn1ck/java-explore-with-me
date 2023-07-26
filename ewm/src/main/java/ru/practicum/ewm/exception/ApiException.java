package ru.practicum.ewm.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ApiException {
    private final HttpStatus status;
    private final String error;
    private final ZonedDateTime dateTime;
}
