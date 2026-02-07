package com.ernoxin.sepjavasdk.exception;

import lombok.Getter;

/**
 * Indicates that SEP responded but the response represents an API-level failure.
 *
 * <p>This includes non-success gateway result codes, malformed payloads, or unsupported response
 * shapes.
 */
@Getter
public class SepApiException extends SepException {
    /**
     * HTTP status code received from SEP.
     */
    private final int httpStatus;
    /**
     * SEP gateway code when available.
     */
    private final Integer gatewayCode;
    /**
     * Human-readable gateway message resolved from response/catalog.
     */
    private final String gatewayMessage;
    /**
     * Raw response body as received from SEP.
     */
    private final String rawBody;

    /**
     * Creates an API exception without root cause.
     *
     * @param httpStatus HTTP status code
     * @param gatewayCode SEP gateway code, may be {@code null}
     * @param gatewayMessage gateway message, may be {@code null}
     * @param rawBody raw HTTP response body, may be {@code null}
     */
    public SepApiException(int httpStatus, Integer gatewayCode, String gatewayMessage, String rawBody) {
        super(buildMessage(httpStatus, gatewayCode, gatewayMessage));
        this.httpStatus = httpStatus;
        this.gatewayCode = gatewayCode;
        this.gatewayMessage = gatewayMessage;
        this.rawBody = rawBody;
    }

    /**
     * Creates an API exception with root cause.
     *
     * @param httpStatus HTTP status code
     * @param gatewayCode SEP gateway code, may be {@code null}
     * @param gatewayMessage gateway message, may be {@code null}
     * @param rawBody raw HTTP response body, may be {@code null}
     * @param cause root cause
     */
    public SepApiException(int httpStatus, Integer gatewayCode, String gatewayMessage, String rawBody, Throwable cause) {
        super(buildMessage(httpStatus, gatewayCode, gatewayMessage), cause);
        this.httpStatus = httpStatus;
        this.gatewayCode = gatewayCode;
        this.gatewayMessage = gatewayMessage;
        this.rawBody = rawBody;
    }

    private static String buildMessage(int httpStatus, Integer gatewayCode, String gatewayMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("SEP API error");
        builder.append(" (http ").append(httpStatus).append(")");
        if (gatewayCode != null) {
            builder.append(" code ").append(gatewayCode);
        }
        if (gatewayMessage != null && !gatewayMessage.isBlank()) {
            builder.append(": ").append(gatewayMessage);
        }
        return builder.toString();
    }
}
