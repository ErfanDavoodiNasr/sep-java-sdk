package com.ernoxin.sepjavasdk.exception;

public class SepValidationException extends SepException {
    public SepValidationException(String message) {
        super(message);
    }

    public SepValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
