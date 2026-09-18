package com.vityarthi.library.exception;

/**
 * Exception thrown when a persistence or database transaction failure occurs.
 */
public class DatabaseOperationException extends LibraryException {
    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
