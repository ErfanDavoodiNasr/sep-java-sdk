package com.ernoxin.sepjavasdk.exception;

/**
 * Indicates callback parsing/validation failure for SEP return parameters.
 */
public class SepCallbackException extends SepException {
    /**
     * Creates a callback exception with message.
     *
     * @param message callback parsing error description
     */
    public SepCallbackException(String message) {
        super(message);
    }
}
