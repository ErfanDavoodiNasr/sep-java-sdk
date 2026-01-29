package com.ernoxin.sepjavasdk.config;

import com.ernoxin.sepjavasdk.exception.SepValidationException;
import com.ernoxin.sepjavasdk.support.SepValidation;

import java.net.URI;
import java.time.Duration;

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
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);
    public static final URI DEFAULT_BASE_URL = URI.create("https://sep.shaparak.ir");
    public static final boolean DEFAULT_RETRY_ENABLED = false;
    public static final int DEFAULT_RETRY_MAX_ATTEMPTS = 1;
    public static final Duration DEFAULT_RETRY_BACKOFF = Duration.ZERO;
    public static final String DEFAULT_USER_AGENT = "SepJavaSdk";
    public static final int DEFAULT_MIN_TOKEN_EXPIRY_IN_MIN = 20;
    public static final int DEFAULT_MAX_TOKEN_EXPIRY_IN_MIN = 3600;
    public static final int DEFAULT_MAX_SETTLEMENT_ITEMS = 9;
    public static final int DEFAULT_MAX_HASHED_CARD_COUNT = 10;
    public static final int DEFAULT_MAX_RES_NUM_LENGTH = 50;

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

    public static Builder builder(String terminalId) {
        return new Builder(terminalId);
    }

    public URI baseUrl() {
        return SepValidation.normalizeBaseUrl(baseUrl);
    }

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

        public Builder(String terminalId) {
            this.terminalId = terminalId;
        }

        public Builder callbackUrl(URI callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder retryEnabled(boolean retryEnabled) {
            this.retryEnabled = retryEnabled;
            return this;
        }

        public Builder retryMaxAttempts(int retryMaxAttempts) {
            this.retryMaxAttempts = retryMaxAttempts;
            return this;
        }

        public Builder retryBackoff(Duration retryBackoff) {
            this.retryBackoff = retryBackoff;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder minTokenExpiryInMin(int minTokenExpiryInMin) {
            this.minTokenExpiryInMin = minTokenExpiryInMin;
            return this;
        }

        public Builder maxTokenExpiryInMin(int maxTokenExpiryInMin) {
            this.maxTokenExpiryInMin = maxTokenExpiryInMin;
            return this;
        }

        public Builder maxSettlementItems(int maxSettlementItems) {
            this.maxSettlementItems = maxSettlementItems;
            return this;
        }

        public Builder maxHashedCardCount(int maxHashedCardCount) {
            this.maxHashedCardCount = maxHashedCardCount;
            return this;
        }

        public Builder maxResNumLength(int maxResNumLength) {
            this.maxResNumLength = maxResNumLength;
            return this;
        }

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
