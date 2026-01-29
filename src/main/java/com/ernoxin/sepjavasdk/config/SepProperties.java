package com.ernoxin.sepjavasdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@Setter
@Getter
@ConfigurationProperties(prefix = "sep")
public class SepProperties {
    private String terminalId;
    private URI callbackUrl;
    private URI baseUrl = SepConfig.DEFAULT_BASE_URL;
    private Timeout timeout = new Timeout();
    private Retry retry = new Retry();
    private Http http = new Http();
    private int minTokenExpiryInMin = SepConfig.DEFAULT_MIN_TOKEN_EXPIRY_IN_MIN;
    private int maxTokenExpiryInMin = SepConfig.DEFAULT_MAX_TOKEN_EXPIRY_IN_MIN;
    private int maxSettlementItems = SepConfig.DEFAULT_MAX_SETTLEMENT_ITEMS;
    private int maxHashedCardCount = SepConfig.DEFAULT_MAX_HASHED_CARD_COUNT;
    private int maxResNumLength = SepConfig.DEFAULT_MAX_RES_NUM_LENGTH;

    public SepConfig toConfig() {
        Timeout timeoutValue = timeout != null ? timeout : new Timeout();
        Retry retryValue = retry != null ? retry : new Retry();
        Http httpValue = http != null ? http : new Http();
        return new SepConfig(
                terminalId,
                callbackUrl,
                timeoutValue.getConnect(),
                timeoutValue.getRead(),
                baseUrl,
                retryValue.isEnabled(),
                retryValue.getMaxAttempts(),
                retryValue.getBackoff(),
                httpValue.getUserAgent(),
                minTokenExpiryInMin,
                maxTokenExpiryInMin,
                maxSettlementItems,
                maxHashedCardCount,
                maxResNumLength
        );
    }

    @Setter
    @Getter
    public static class Timeout {
        private Duration connect = SepConfig.DEFAULT_CONNECT_TIMEOUT;
        private Duration read = SepConfig.DEFAULT_READ_TIMEOUT;
    }

    @Setter
    @Getter
    public static class Retry {
        private boolean enabled = SepConfig.DEFAULT_RETRY_ENABLED;
        private int maxAttempts = SepConfig.DEFAULT_RETRY_MAX_ATTEMPTS;
        private Duration backoff = SepConfig.DEFAULT_RETRY_BACKOFF;
    }

    @Setter
    @Getter
    public static class Http {
        private String userAgent = SepConfig.DEFAULT_USER_AGENT;
    }
}
