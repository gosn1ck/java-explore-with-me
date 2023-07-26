package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = ClientErrorException.class)
    public ResponseEntity<ApiException> handleException(ClientErrorException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(CONFLICT, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, CONFLICT);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ApiException> handleException(NotFoundException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(NOT_FOUND, e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiException> handleException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        String name = e.getName();
        String type = e.getRequiredType().getSimpleName();
        Object value = e.getValue();
        String errorMessage = String.format("'%s' should be a valid '%s' and '%s' isn't",
                name, type, value);

        ApiException exception = new ApiException(BAD_REQUEST, errorMessage, ZonedDateTime.now());

        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String errorMessage = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList()).toString();
        ApiException exception = new ApiException(BAD_REQUEST, errorMessage, ZonedDateTime.now());

        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

}
