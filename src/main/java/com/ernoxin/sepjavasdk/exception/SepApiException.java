package com.ernoxin.sepjavasdk.exception;

import lombok.Getter;

@Getter
public class SepApiException extends SepException {
    private final int httpStatus;
    private final Integer gatewayCode;
    private final String gatewayMessage;
    private final String rawBody;

    public SepApiException(int httpStatus, Integer gatewayCode, String gatewayMessage, String rawBody) {
        super(buildMessage(httpStatus, gatewayCode, gatewayMessage));
        this.httpStatus = httpStatus;
        this.gatewayCode = gatewayCode;
        this.gatewayMessage = gatewayMessage;
        this.rawBody = rawBody;
    }

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
