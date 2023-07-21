package ru.practicum.stats.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiException> handleException(BadRequestException e) {
        log.error(e.getMessage(), e);
        ApiException exception = new ApiException(BAD_REQUEST, e.getMessage(), ZonedDateTime.now());
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

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ApiException> handleMissingParams(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        String name = e.getParameterName();
        String errorMessage = name + " parameter is missing";
        ApiException exception = new ApiException(BAD_REQUEST, errorMessage, ZonedDateTime.now());

        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiException> handleException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        var errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        ApiException exception = new ApiException(BAD_REQUEST, errors.toString(), ZonedDateTime.now());
        return new ResponseEntity<>(exception, BAD_REQUEST);
    }

}
