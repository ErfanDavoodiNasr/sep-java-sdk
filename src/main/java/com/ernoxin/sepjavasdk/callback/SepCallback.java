package com.ernoxin.sepjavasdk.callback;

/**
 * Parsed callback payload returned by SEP after the customer returns from the payment page.
 *
 * <p>Most fields are optional in gateway callbacks and may be {@code null}. For successful
 * payments ({@link #isOk()}), {@code refNum} should be present and is required by
 * {@code SepClient.parseCallback(...)}.
 *
 * @param status high-level callback status resolved from {@code State}/{@code Status}
 * @param statusCode numeric status code from callback {@code Status} parameter, when provided
 * @param token gateway token associated with this payment attempt
 * @param resNum merchant order identifier sent during token request
 * @param refNum SEP reference number used for verification/reverse operations
 * @param traceNo gateway trace number
 * @param terminalId terminal identifier returned by SEP
 * @param mid merchant identifier returned by SEP
 * @param rrn retrieval reference number (RRN) returned by SEP
 * @param amount transaction amount returned in callback (gateway integer amount unit)
 * @param wage wage amount returned in callback, if applicable
 * @param affectiveAmount effective amount returned by SEP (may differ for discount terminals)
 * @param securePan masked PAN returned by SEP
 * @param hashedCardNumber hashed card number returned by SEP
 */
public record SepCallback(
        SepCallbackStatus status,
        Integer statusCode,
        String token,
        String resNum,
        String refNum,
        String traceNo,
        String terminalId,
        String mid,
        String rrn,
        Long amount,
        Long wage,
        Long affectiveAmount,
        String securePan,
        String hashedCardNumber
) {
    /**
     * Indicates whether the callback represents a successful payment state.
     *
     * @return {@code true} when {@link #status()} is {@link SepCallbackStatus#OK}; otherwise
     * {@code false}
     */
    public boolean isOk() {
        return status != null && status.isOk();
    }
}
