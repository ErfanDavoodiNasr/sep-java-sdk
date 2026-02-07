package com.ernoxin.sepjavasdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

/**
 * Spring Boot bindable properties for SEP SDK under {@code sep.*} prefix.
 *
 * <p>This type is converted to immutable runtime {@link SepConfig} through {@link #toConfig()}.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "sep")
public class SepProperties {
    /**
     * Merchant terminal identifier (numeric).
     */
    private String terminalId;
    /**
     * Default callback URL used after payment.
     */
    private URI callbackUrl;
    /**
     * SEP gateway base URL.
     */
    private URI baseUrl = SepConfig.DEFAULT_BASE_URL;
    /**
     * Timeout configuration group.
     */
    private Timeout timeout = new Timeout();
    /**
     * Retry configuration group.
     */
    private Retry retry = new Retry();
    /**
     * HTTP configuration group.
     */
    private Http http = new Http();
    /**
     * Minimum token expiry bound (minutes).
     */
    private int minTokenExpiryInMin = SepConfig.DEFAULT_MIN_TOKEN_EXPIRY_IN_MIN;
    /**
     * Maximum token expiry bound (minutes).
     */
    private int maxTokenExpiryInMin = SepConfig.DEFAULT_MAX_TOKEN_EXPIRY_IN_MIN;
    /**
     * Maximum number of settlement split items per token request.
     */
    private int maxSettlementItems = SepConfig.DEFAULT_MAX_SETTLEMENT_ITEMS;
    /**
     * Maximum number of hashed cards per token request.
     */
    private int maxHashedCardCount = SepConfig.DEFAULT_MAX_HASHED_CARD_COUNT;
    /**
     * Maximum length for {@code resNum}/{@code resNum1..4}.
     */
    private int maxResNumLength = SepConfig.DEFAULT_MAX_RES_NUM_LENGTH;

    /**
     * Converts bound properties into validated immutable {@link SepConfig}.
     *
     * <p>If nested property groups are missing, defaults are applied.
     *
     * @return validated runtime configuration
     */
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

    /**
     * Timeout-related configuration group.
     */
    @Setter
    @Getter
    public static class Timeout {
        /**
         * TCP/TLS connect timeout.
         */
        private Duration connect = SepConfig.DEFAULT_CONNECT_TIMEOUT;
        /**
         * Response read timeout.
         */
        private Duration read = SepConfig.DEFAULT_READ_TIMEOUT;
    }

    /**
     * Retry-related configuration group.
     */
    @Setter
    @Getter
    public static class Retry {
        /**
         * Enables retry on transport errors.
         */
        private boolean enabled = SepConfig.DEFAULT_RETRY_ENABLED;
        /**
         * Total attempts when retry is enabled.
         */
        private int maxAttempts = SepConfig.DEFAULT_RETRY_MAX_ATTEMPTS;
        /**
         * Fixed delay between retry attempts.
         */
        private Duration backoff = SepConfig.DEFAULT_RETRY_BACKOFF;
    }

    /**
     * HTTP-level configuration group.
     */
    @Setter
    @Getter
    public static class Http {
        /**
         * HTTP {@code User-Agent} header value.
         */
        private String userAgent = SepConfig.DEFAULT_USER_AGENT;
    }
}
