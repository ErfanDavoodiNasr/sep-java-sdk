package com.ernoxin.sepjavasdk.http;

/**
 * Supported SEP response validation modes.
 */
public enum SepResponseType {
    /**
     * Token API response schema ({@code status}/{@code token}/{@code errorCode}).
     */
    TOKEN,
    /**
     * Verify/reverse response schema ({@code ResultCode}/{@code Success}).
     */
    TRANSACTION
}
