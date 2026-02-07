package com.ernoxin.sepjavasdk.model;

/**
 * SEP response for transaction verification.
 *
 * @param transactionDetail transaction details when provided by gateway
 * @param resultCode gateway result code ({@code 0} indicates success)
 * @param resultDescription gateway result description
 * @param success gateway success flag
 */
public record VerifyResult(
        TransactionDetail transactionDetail,
        int resultCode,
        String resultDescription,
        boolean success
) {
}
