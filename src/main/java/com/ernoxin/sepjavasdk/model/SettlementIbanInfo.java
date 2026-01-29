package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SettlementIbanInfo(
        @JsonProperty("IBAN") String iban,
        long amount,
        String purchaseId
) {
}
