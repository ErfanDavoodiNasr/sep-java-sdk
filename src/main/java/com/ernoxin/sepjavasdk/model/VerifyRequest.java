package com.ernoxin.sepjavasdk.model;

/**
 * Request payload for transaction verification.
 *
 * @param refNum SEP reference number obtained from callback
 */
public record VerifyRequest(String refNum) {
}
