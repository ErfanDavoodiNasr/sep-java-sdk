package com.ernoxin.sepjavasdk.exception;

/**
 * Base unchecked exception type for SEP SDK failures.
 */
public class SepException extends RuntimeException {
    /**
     * Creates a new exception with message.
     *
     * @param message failure message
     */
    public SepException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with message and cause.
     *
     * @param message failure message
     * @param cause root cause
     */
    public SepException(String message, Throwable cause) {
        super(message, cause);
    }
}
