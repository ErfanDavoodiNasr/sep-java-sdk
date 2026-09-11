package com.ernoxin.sepjavasdk.client;

import com.ernoxin.sepjavasdk.config.SepConfig;
import com.ernoxin.sepjavasdk.exception.SepValidationException;
import com.ernoxin.sepjavasdk.model.SettlementIbanInfo;
import com.ernoxin.sepjavasdk.model.TokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SepSettlementValidationTest {
    private SepClient client;

    @BeforeEach
    void setUp() {
        SepConfig config = SepConfig.builder("12345678")
                .callbackUrl(URI.create("https://example.com/callback"))
                .build();
        client = new SepClient(config);
    }

    @Test
    void rejectsSettlementWhenAmountsDoNotMatchTokenAmount() {
        TokenRequest request = TokenRequest.builder(12_000, "ORDER-1")
                .settlementIbanInfo(List.of(
                        new SettlementIbanInfo("IR111111111111111111111111", 7_000, "0".repeat(30)),
                        new SettlementIbanInfo("IR222222222222222222222222", 4_000, "0".repeat(30))
                ))
                .build();
        SepValidationException ex = assertThrows(
                SepValidationException.class,
                () -> client.requestToken(request)
        );
        assertTrue(ex.getMessage().contains("sum to token amount"));
    }
}
