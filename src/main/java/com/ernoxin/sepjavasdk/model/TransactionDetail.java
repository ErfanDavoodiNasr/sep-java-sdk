package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Detailed transaction payload returned by verify/reverse APIs.
 *
 * @param rrn             retrieval reference number
 * @param refNum          SEP reference number
 * @param maskedPan       masked PAN value
 * @param hashedPan       hashed PAN value
 * @param terminalNumber  terminal number used for transaction; {@code null} when absent
 * @param originalAmount  original transaction amount (gateway integer unit); {@code null} when absent
 * @param affectiveAmount effective amount after gateway adjustments; {@code null} when absent
 * @param straceDate      trace date string from SEP
 * @param straceNo        trace number string from SEP
 */
public record TransactionDetail(
        @JsonProperty("RRN") String rrn,
        @JsonProperty("RefNum") String refNum,
        @JsonProperty("MaskedPan") String maskedPan,
        @JsonProperty("HashedPan") String hashedPan,
        @JsonProperty("TerminalNumber") Long terminalNumber,
        @JsonProperty("OrginalAmount") Long originalAmount,
        @JsonProperty("AffectiveAmount") Long affectiveAmount,
        @JsonProperty("StraceDate") String straceDate,
        @JsonProperty("StraceNo") String straceNo
) {
}
