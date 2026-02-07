package com.ernoxin.sepjavasdk.support;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Catalog of known SEP gateway codes to human-readable messages.
 */
@UtilityClass
public class SepErrorCatalog {
    private static final Map<Integer, String> TOKEN_MESSAGES = Map.ofEntries(
            Map.entry(1, "Canceled by user"),
            Map.entry(2, "Payment succeeded"),
            Map.entry(3, "Payment failed"),
            Map.entry(4, "Session is null"),
            Map.entry(5, "Invalid parameters"),
            Map.entry(8, "Merchant IP address is invalid"),
            Map.entry(10, "Token not found"),
            Map.entry(11, "Token required"),
            Map.entry(12, "Terminal not found"),
            Map.entry(21, "Multisettle policy errors")
    );

    private static final Map<Integer, String> TRANSACTION_MESSAGES = Map.ofEntries(
            Map.entry(0, "Success"),
            Map.entry(-2, "Transaction not found"),
            Map.entry(-6, "Maximum time for verify has expired"),
            Map.entry(2, "Duplicate request"),
            Map.entry(-104, "Terminal is inactive"),
            Map.entry(-105, "Terminal not found"),
            Map.entry(-106, "IP address is invalid"),
            Map.entry(5, "Transaction is reversed")
    );

    /**
     * Resolves a token API code to catalog message.
     *
     * @param code SEP token response code
     * @return catalog message, or {@code null} when unknown
     */
    public static String messageForToken(Integer code) {
        if (code == null) {
            return null;
        }
        return TOKEN_MESSAGES.get(code);
    }

    /**
     * Resolves verify/reverse response code to catalog message.
     *
     * @param code SEP transaction response code
     * @return catalog message, or {@code null} when unknown
     */
    public static String messageForTransaction(Integer code) {
        if (code == null) {
            return null;
        }
        return TRANSACTION_MESSAGES.get(code);
    }
}
