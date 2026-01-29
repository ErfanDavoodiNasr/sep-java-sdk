package com.ernoxin.sepjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResult(
        @JsonProperty("status") int status,
        @JsonProperty("token") String token
) {
}
