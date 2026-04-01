package ru.iskalkin.moneycoach.exception;

public class OperationNotFoundException extends RuntimeException {
    public OperationNotFoundException(Long id) {
        super("Operation not found: " + id);
    }
}
