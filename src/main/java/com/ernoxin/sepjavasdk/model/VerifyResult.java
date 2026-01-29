package com.ernoxin.sepjavasdk.model;

public record VerifyResult(
        TransactionDetail transactionDetail,
        int resultCode,
        String resultDescription,
        boolean success
) {
}
