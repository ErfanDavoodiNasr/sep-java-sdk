package com.ernoxin.sepjavasdk.callback;

import java.util.Locale;

/**
 * Canonical callback status values returned by SEP.
 *
 * <p>Each enum value maps to both a numeric {@code Status} code and a textual {@code State}
 * callback value.
 */
public enum SepCallbackStatus {
    /**
     * Customer canceled the payment on the gateway page.
     */
    CANCELED_BY_USER(1, "CanceledByUser"),
    /**
     * Payment was completed successfully.
     */
    OK(2, "OK"),
    /**
     * Payment failed on gateway side.
     */
    FAILED(3, "Failed"),
    /**
     * Session was not found or already expired.
     */
    SESSION_IS_NULL(4, "SessionIsNull"),
    /**
     * Input parameters were invalid.
     */
    INVALID_PARAMETERS(5, "InvalidParameters"),
    /**
     * Merchant IP was rejected by SEP policy.
     */
    MERCHANT_IP_ADDRESS_IS_INVALID(8, "MerchantIpAddressIsInvalid"),
    /**
     * Submitted token does not exist.
     */
    TOKEN_NOT_FOUND(10, "TokenNotFound"),
    /**
     * Token parameter was required but missing.
     */
    TOKEN_REQUIRED(11, "TokenRequired"),
    /**
     * Terminal identifier is unknown in SEP.
     */
    TERMINAL_NOT_FOUND(12, "TerminalNotFound"),
    /**
     * Multi-settlement policy validation failed.
     */
    MULTISETTLE_POLICY_ERRORS(21, "MultisettlePolicyErrors");

    private final int code;
    private final String gatewayValue;

    SepCallbackStatus(int code, String gatewayValue) {
        this.code = code;
        this.gatewayValue = gatewayValue;
    }

    /**
     * Resolves a callback status from the textual {@code State} parameter.
     *
     * @param state raw callback state value; may be {@code null}
     * @return matching status, or {@code null} when unknown/blank
     */
    public static SepCallbackStatus fromState(String state) {
        if (state == null || state.isBlank()) {
            return null;
        }
        String normalized = state.trim().toLowerCase(Locale.ROOT);
        for (SepCallbackStatus status : values()) {
            if (status.gatewayValue.toLowerCase(Locale.ROOT).equals(normalized)) {
                return status;
            }
        }
        return null;
    }

    /**
     * Resolves a callback status from numeric {@code Status} code.
     *
     * @param code callback status code; may be {@code null}
     * @return matching status, or {@code null} when unknown
     */
    public static SepCallbackStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SepCallbackStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * Returns SEP numeric code associated with this status.
     *
     * @return gateway status code
     */
    public int code() {
        return code;
    }

    /**
     * Returns SEP textual state associated with this status.
     *
     * @return gateway textual value used in callbacks
     */
    public String gatewayValue() {
        return gatewayValue;
    }

    /**
     * Indicates whether this status represents a successful payment.
     *
     * @return {@code true} only for {@link #OK}
     */
    public boolean isOk() {
        return this == OK;
    }
}
