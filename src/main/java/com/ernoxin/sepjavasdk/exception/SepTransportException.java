package com.ernoxin.sepjavasdk.exception;

/**
 * Indicates transport-level communication failure while calling SEP endpoints.
 *
 * <p>This exception represents network/client problems (timeouts, I/O failures, interruptions),
 * not SEP business response codes.
 */
public class SepTransportException extends SepException {
    /**
     * Creates a transport exception.
     *
     * @param message transport failure summary
     * @param cause root cause
     */
    public SepTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
