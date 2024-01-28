package ru.practicum.shareit.exception;

public class WrongAccesException extends RuntimeException {
    public WrongAccesException(String message) {
        super(message);
    }
}
