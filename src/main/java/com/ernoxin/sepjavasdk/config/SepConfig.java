package com.ernoxin.sepjavasdk.config;

import com.ernoxin.sepjavasdk.exception.SepValidationException;
import com.ernoxin.sepjavasdk.support.SepValidation;

import java.net.URI;
import java.time.Duration;

/**
 * Immutable runtime configuration used by SEP SDK components.
 *
 * <p>Instances are validated eagerly (fail-fast). Any invalid value causes a
 * {@link SepValidationException} during construction.
 *
 * @param terminalId          merchant terminal identifier; must be numeric and non-blank
 * @param callbackUrl         default callback URL used when request-specific redirect URL is absent
 * @param connectTimeout      network connect timeout for SEP HTTP calls; positive duration
 * @param readTimeout         network read timeout for SEP HTTP calls; positive duration
 * @param baseUrl             SEP base URL; normalized without trailing slash
 * @param retryEnabled        enables retry on transport errors for safe/read-style calls only;
 *                            token/verify/reverse never retry
 * @param retryMaxAttempts    total retry attempts when retry is enabled; minimum {@code 1}
 * @param retryBackoff        delay between retry attempts; non-negative
 * @param userAgent           HTTP {@code User-Agent} header value
 * @param minTokenExpiryInMin lower bound for token expiry normalization
 * @param maxTokenExpiryInMin upper bound for token expiry normalization
 * @param maxSettlementItems  maximum allowed number of settlement items in token requests
 * @param maxHashedCardCount  maximum allowed number of hashed card numbers in token requests
 * @param maxResNumLength     maximum allowed length for {@code resNum} and related fields
 */
public record SepConfig(
        String terminalId,
        URI callbackUrl,
        Duration connectTimeout,
        Duration readTimeout,
        URI baseUrl,
        boolean retryEnabled,
        int retryMaxAttempts,
        Duration retryBackoff,
        String userAgent,
        int minTokenExpiryInMin,
        int maxTokenExpiryInMin,
        int maxSettlementItems,
        int maxHashedCardCount,
        int maxResNumLength
) {
    /**
     * Default connect timeout ({@code 10s}).
     */
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    /**
     * Default read timeout ({@code 30s}).
     */
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);
    /**
     * Default SEP base URL.
     */
    public static final URI DEFAULT_BASE_URL = URI.create("https://sep.shaparak.ir");
    /**
     * Retry disabled by default.
     */
    public static final boolean DEFAULT_RETRY_ENABLED = false;
    /**
     * Default max retry attempts ({@code 1}).
     */
    public static final int DEFAULT_RETRY_MAX_ATTEMPTS = 1;
    /**
     * Default retry backoff ({@code 0ms}).
     */
    public static final Duration DEFAULT_RETRY_BACKOFF = Duration.ZERO;
    /**
     * Default HTTP {@code User-Agent} value.
     */
    public static final String DEFAULT_USER_AGENT = "SepJavaSdk";
    /**
     * Default minimum token expiry in minutes.
     */
    public static final int DEFAULT_MIN_TOKEN_EXPIRY_IN_MIN = 20;
    /**
     * Default maximum token expiry in minutes.
     */
    public static final int DEFAULT_MAX_TOKEN_EXPIRY_IN_MIN = 3600;
    /**
     * Default maximum number of settlement items.
     */
    public static final int DEFAULT_MAX_SETTLEMENT_ITEMS = 9;
    /**
     * Default maximum number of hashed card constraints.
     */
    public static final int DEFAULT_MAX_HASHED_CARD_COUNT = 10;
    /**
     * Default maximum length for {@code resNum}/{@code resNum1..4}.
     */
    public static final int DEFAULT_MAX_RES_NUM_LENGTH = 50;

    /**
     * Creates and validates a new immutable configuration.
     *
     * <p>Null timeout and retry backoff values are replaced by defaults.
     *
     * @throws SepValidationException when any value violates SDK constraints
     */
    public SepConfig {
        if (terminalId == null || terminalId.isBlank()) {
            throw new SepValidationException("terminalId is required");
        }
        terminalId = terminalId.trim();
        SepValidation.requireTerminalId(terminalId);
        if (callbackUrl == null) {
            throw new SepValidationException("callbackUrl is required");
        }
        SepValidation.requireHttpUri(callbackUrl, "callbackUrl");
        if (connectTimeout == null) {
            connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        }
        if (readTimeout == null) {
            readTimeout = DEFAULT_READ_TIMEOUT;
        }
        if (connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new SepValidationException("connectTimeout must be positive");
        }
        if (readTimeout.isZero() || readTimeout.isNegative()) {
            throw new SepValidationException("readTimeout must be positive");
        }
        if (baseUrl == null) {
            baseUrl = DEFAULT_BASE_URL;
        }
        SepValidation.requireHttpUri(baseUrl, "baseUrl");
        baseUrl = SepValidation.normalizeBaseUrl(baseUrl);
        if (retryBackoff == null) {
            retryBackoff = DEFAULT_RETRY_BACKOFF;
        }
        if (retryMaxAttempts <= 0) {
            throw new SepValidationException("retryMaxAttempts must be at least 1");
        }
        if (retryBackoff.isNegative()) {
            throw new SepValidationException("retryBackoff must be non-negative");
        }
        if (userAgent == null || userAgent.isBlank()) {
            userAgent = DEFAULT_USER_AGENT;
        }
        userAgent = userAgent.trim();
        if (minTokenExpiryInMin <= 0) {
            throw new SepValidationException("minTokenExpiryInMin must be positive");
        }
        if (maxTokenExpiryInMin <= 0) {
            throw new SepValidationException("maxTokenExpiryInMin must be positive");
        }
        if (maxTokenExpiryInMin < minTokenExpiryInMin) {
            throw new SepValidationException("maxTokenExpiryInMin must be greater than or equal to minTokenExpiryInMin");
        }
        if (maxSettlementItems <= 0) {
            throw new SepValidationException("maxSettlementItems must be positive");
        }
        if (maxHashedCardCount <= 0) {
            throw new SepValidationException("maxHashedCardCount must be positive");
        }
        if (maxResNumLength <= 0) {
            throw new SepValidationException("maxResNumLength must be positive");
        }
    }

    /**
     * Starts a builder with the required terminal identifier.
     *
     * @param terminalId merchant terminal identifier
     * @return mutable builder for creating {@link SepConfig}
     */
    public static Builder builder(String terminalId) {
        return new Builder(terminalId);
    }

    /**
     * Returns normalized base URL without trailing slash.
     *
     * @return normalized SEP base URL
     */
    public URI baseUrl() {
        return SepValidation.normalizeBaseUrl(baseUrl);
    }

    /**
     * Mutable builder for {@link SepConfig}.
     */
    public static final class Builder {
        private final String terminalId;
        private URI callbackUrl;
        private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private Duration readTimeout = DEFAULT_READ_TIMEOUT;
        private URI baseUrl = DEFAULT_BASE_URL;
        private boolean retryEnabled = DEFAULT_RETRY_ENABLED;
        private int retryMaxAttempts = DEFAULT_RETRY_MAX_ATTEMPTS;
        private Duration retryBackoff = DEFAULT_RETRY_BACKOFF;
        private String userAgent = DEFAULT_USER_AGENT;
        private int minTokenExpiryInMin = DEFAULT_MIN_TOKEN_EXPIRY_IN_MIN;
        private int maxTokenExpiryInMin = DEFAULT_MAX_TOKEN_EXPIRY_IN_MIN;
        private int maxSettlementItems = DEFAULT_MAX_SETTLEMENT_ITEMS;
        private int maxHashedCardCount = DEFAULT_MAX_HASHED_CARD_COUNT;
        private int maxResNumLength = DEFAULT_MAX_RES_NUM_LENGTH;

        /**
         * Creates a builder with required terminal identifier.
         *
         * @param terminalId merchant terminal identifier
         */
        public Builder(String terminalId) {
            this.terminalId = terminalId;
        }

        /**
         * Sets default callback URL.
         *
         * @param callbackUrl callback endpoint for payment return
         * @return this builder
         */
        public Builder callbackUrl(URI callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        /**
         * Sets network connect timeout.
         *
         * @param connectTimeout connect timeout duration
         * @return this builder
         */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets network read timeout.
         *
         * @param readTimeout read timeout duration
         * @return this builder
         */
        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets SEP base URL.
         *
         * @param baseUrl base URL for all SEP API endpoints
         * @return this builder
         */
        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Enables or disables retry logic on transport failures.
         *
         * @param retryEnabled retry toggle
         * @return this builder
         */
        public Builder retryEnabled(boolean retryEnabled) {
            this.retryEnabled = retryEnabled;
            return this;
        }

        /**
         * Sets maximum retry attempts.
         *
         * @param retryMaxAttempts total attempts; must be at least {@code 1}
         * @return this builder
         */
        public Builder retryMaxAttempts(int retryMaxAttempts) {
            this.retryMaxAttempts = retryMaxAttempts;
            return this;
        }

        /**
         * Sets fixed retry backoff delay.
         *
         * @param retryBackoff delay between attempts
         * @return this builder
         */
        public Builder retryBackoff(Duration retryBackoff) {
            this.retryBackoff = retryBackoff;
            return this;
        }

        /**
         * Sets HTTP {@code User-Agent} header value.
         *
         * @param userAgent user agent string
         * @return this builder
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Sets minimum token expiry clamp value.
         *
         * @param minTokenExpiryInMin lower bound in minutes
         * @return this builder
         */
        public Builder minTokenExpiryInMin(int minTokenExpiryInMin) {
            this.minTokenExpiryInMin = minTokenExpiryInMin;
            return this;
        }

        /**
         * Sets maximum token expiry clamp value.
         *
         * @param maxTokenExpiryInMin upper bound in minutes
         * @return this builder
         */
        public Builder maxTokenExpiryInMin(int maxTokenExpiryInMin) {
            this.maxTokenExpiryInMin = maxTokenExpiryInMin;
            return this;
        }

        /**
         * Sets maximum number of settlement items.
         *
         * @param maxSettlementItems item limit
         * @return this builder
         */
        public Builder maxSettlementItems(int maxSettlementItems) {
            this.maxSettlementItems = maxSettlementItems;
            return this;
        }

        /**
         * Sets maximum number of hashed card constraints.
         *
         * @param maxHashedCardCount hashed card limit
         * @return this builder
         */
        public Builder maxHashedCardCount(int maxHashedCardCount) {
            this.maxHashedCardCount = maxHashedCardCount;
            return this;
        }

        /**
         * Sets maximum length for {@code resNum}-like fields.
         *
         * @param maxResNumLength character limit
         * @return this builder
         */
        public Builder maxResNumLength(int maxResNumLength) {
            this.maxResNumLength = maxResNumLength;
            return this;
        }

        /**
         * Builds an immutable validated {@link SepConfig}.
         *
         * @return validated configuration instance
         * @throws SepValidationException when any field is invalid
         */
        public SepConfig build() {
            return new SepConfig(
                    terminalId,
                    callbackUrl,
                    connectTimeout,
                    readTimeout,
                    baseUrl,
                    retryEnabled,
                    retryMaxAttempts,
                    retryBackoff,
                    userAgent,
                    minTokenExpiryInMin,
                    maxTokenExpiryInMin,
                    maxSettlementItems,
                    maxHashedCardCount,
                    maxResNumLength
            );
        }
    }
}
