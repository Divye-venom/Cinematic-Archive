package com.vityarthi.library.exception;

/**
 * Base runtime exception for the Library & Archive application domain.
 */
public class LibraryException extends RuntimeException {
    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
