package ru.iskalkin.moneycoach.exception;

public class OperationAlreadyStornedException extends RuntimeException {
    public OperationAlreadyStornedException(Long id) {
        super("Operation already storned: " + id);
    }
}
