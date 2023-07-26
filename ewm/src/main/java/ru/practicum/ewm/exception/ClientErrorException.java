package ru.practicum.ewm.exception;

public class ClientErrorException extends RuntimeException {

    public ClientErrorException(String message, String part) {
        super(String.format(message, part));
    }
}
