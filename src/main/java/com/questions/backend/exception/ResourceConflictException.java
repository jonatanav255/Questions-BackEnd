package com.questions.backend.exception;

/**
 * Exception thrown when an operation conflicts with the current state of a resource.
 * For example, trying to delete a category that has questions.
 * Results in HTTP 409 Conflict response.
 */
public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message) {
        super(message);
    }
}
