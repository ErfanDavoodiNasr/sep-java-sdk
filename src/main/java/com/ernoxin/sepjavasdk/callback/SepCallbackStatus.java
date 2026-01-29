package com.ernoxin.sepjavasdk.callback;

import java.util.Locale;

public enum SepCallbackStatus {
    CANCELED_BY_USER(1, "CanceledByUser"),
    OK(2, "OK"),
    FAILED(3, "Failed"),
    SESSION_IS_NULL(4, "SessionIsNull"),
    INVALID_PARAMETERS(5, "InvalidParameters"),
    MERCHANT_IP_ADDRESS_IS_INVALID(8, "MerchantIpAddressIsInvalid"),
    TOKEN_NOT_FOUND(10, "TokenNotFound"),
    TOKEN_REQUIRED(11, "TokenRequired"),
    TERMINAL_NOT_FOUND(12, "TerminalNotFound"),
    MULTISETTLE_POLICY_ERRORS(21, "MultisettlePolicyErrors");

    private final int code;
    private final String gatewayValue;

    SepCallbackStatus(int code, String gatewayValue) {
        this.code = code;
        this.gatewayValue = gatewayValue;
    }

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

    public int code() {
        return code;
    }

    public String gatewayValue() {
        return gatewayValue;
    }

    public boolean isOk() {
        return this == OK;
    }
}
