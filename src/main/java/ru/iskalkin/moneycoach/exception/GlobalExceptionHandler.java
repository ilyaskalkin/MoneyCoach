package ru.iskalkin.moneycoach.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OperationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(OperationNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(OperationAlreadyStornedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAlreadyStorned(OperationAlreadyStornedException ex) {
        return Map.of("error", ex.getMessage());
    }
}
