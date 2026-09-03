package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Settlement target entry used for multi-settlement token requests.
 *
 * @param iban       destination IBAN in Iranian format ({@code IR} + 24 digits)
 * @param amount     settlement amount in gateway integer amount unit (commonly IRR)
 * @param purchaseId settlement item identifier expected by SEP
 */
public record SettlementIbanInfo(
        @JsonProperty("IBAN") String iban,
        long amount,
        String purchaseId
) {
}
