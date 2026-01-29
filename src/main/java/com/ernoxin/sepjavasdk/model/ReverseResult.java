package com.ernoxin.sepjavasdk.model;

public record ReverseResult(
        TransactionDetail transactionDetail,
        int resultCode,
        String resultDescription,
        boolean success
) {
}
