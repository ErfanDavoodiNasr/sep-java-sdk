package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SepTranType {
    GOVERNMENT("Government");

    private final String value;

    SepTranType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
