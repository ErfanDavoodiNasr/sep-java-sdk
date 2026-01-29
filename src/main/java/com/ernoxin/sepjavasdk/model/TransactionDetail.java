package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionDetail(
        @JsonProperty("RRN") String rrn,
        @JsonProperty("RefNum") String refNum,
        @JsonProperty("MaskedPan") String maskedPan,
        @JsonProperty("HashedPan") String hashedPan,
        @JsonProperty("TerminalNumber") long terminalNumber,
        @JsonProperty("OrginalAmount") long originalAmount,
        @JsonProperty("AffectiveAmount") long affectiveAmount,
        @JsonProperty("StraceDate") String straceDate,
        @JsonProperty("StraceNo") String straceNo
) {
}
