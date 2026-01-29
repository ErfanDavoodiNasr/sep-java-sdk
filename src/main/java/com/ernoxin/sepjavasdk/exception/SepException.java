package com.ernoxin.sepjavasdk.exception;

public class SepException extends RuntimeException {
    public SepException(String message) {
        super(message);
    }

    public SepException(String message, Throwable cause) {
        super(message, cause);
    }
}
