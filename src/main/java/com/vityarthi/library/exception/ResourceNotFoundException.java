package com.vityarthi.library.exception;

/**
 * Exception thrown when a requested resource (such as a Film or Review) cannot be found.
 */
public class ResourceNotFoundException extends LibraryException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
