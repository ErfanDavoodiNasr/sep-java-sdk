package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SEP response for token generation endpoint.
 *
 * @param status gateway status code ({@code 1} indicates success for token API)
 * @param token  generated token used for redirect/payment
 */
public record TokenResult(
        @JsonProperty("status") int status,
        @JsonProperty("token") String token
) {
}
