package com.ernoxin.sepjavasdk.model;

/**
 * SEP response for transaction reverse operation.
 *
 * @param transactionDetail transaction details when provided by gateway
 * @param resultCode        gateway result code ({@code 0} indicates success)
 * @param resultDescription gateway result description
 * @param success           gateway success flag
 */
public record ReverseResult(
        TransactionDetail transactionDetail,
        int resultCode,
        String resultDescription,
        boolean success
) {
}
