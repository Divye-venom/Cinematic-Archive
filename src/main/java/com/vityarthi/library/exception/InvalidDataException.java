package com.vityarthi.library.exception;

/**
 * Exception thrown when domain data fails validation checks (e.g., blank titles,
 * invalid year ranges, or ratings outside 1.0 to 5.0).
 */
public class InvalidDataException extends LibraryException {
    public InvalidDataException(String message) {
        super(message);
    }
}
