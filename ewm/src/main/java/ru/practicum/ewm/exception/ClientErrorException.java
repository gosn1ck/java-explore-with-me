package ru.practicum.ewm.exception;

public class ClientErrorException extends RuntimeException {

    public ClientErrorException(String message) {
        super(message);
    }

    public ClientErrorException(String message, String part) {
        super(String.format(message, part));
    }

    public ClientErrorException(String message, Long id) {
        super(String.format(message, id));
    }

    public ClientErrorException(String message, Long firstId, Long secondId) {
        super(String.format(message, firstId, secondId));
    }

}