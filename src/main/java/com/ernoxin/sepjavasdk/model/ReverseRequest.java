package com.ernoxin.sepjavasdk.model;

/**
 * Request payload for transaction reverse operation.
 *
 * @param refNum SEP reference number of previously verified transaction
 */
public record ReverseRequest(String refNum) {
}
