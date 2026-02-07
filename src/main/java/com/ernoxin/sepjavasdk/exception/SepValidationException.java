package com.ernoxin.sepjavasdk.exception;

/**
 * Indicates that SDK input/configuration validation failed before or during request preparation.
 */
public class SepValidationException extends SepException {
    /**
     * Creates a validation exception with message.
     *
     * @param message validation error description
     */
    public SepValidationException(String message) {
        super(message);
    }

    /**
     * Creates a validation exception with message and cause.
     *
     * @param message validation error description
     * @param cause root cause
     */
    public SepValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
