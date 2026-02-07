package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported transaction type flags accepted by SEP token API.
 */
public enum SepTranType {
    /**
     * Government transaction mode.
     *
     * <p>When used, {@code settlementIbanInfo} is required.
     */
    GOVERNMENT("Government");

    private final String value;

    SepTranType(String value) {
        this.value = value;
    }

    /**
     * Serialized value expected by SEP API.
     *
     * @return gateway enum token
     */
    @JsonValue
    public String value() {
        return value;
    }
}
