package com.ernoxin.sepjavasdk.client;

import com.ernoxin.sepjavasdk.callback.SepCallback;
import com.ernoxin.sepjavasdk.callback.SepCallbackStatus;
import com.ernoxin.sepjavasdk.config.SepConfig;
import com.ernoxin.sepjavasdk.exception.SepApiException;
import com.ernoxin.sepjavasdk.exception.SepCallbackException;
import com.ernoxin.sepjavasdk.exception.SepTransportException;
import com.ernoxin.sepjavasdk.exception.SepValidationException;
import com.ernoxin.sepjavasdk.http.SepHttpClient;
import com.ernoxin.sepjavasdk.http.SepResponseType;
import com.ernoxin.sepjavasdk.model.*;
import com.ernoxin.sepjavasdk.support.SepEndpoints;
import com.ernoxin.sepjavasdk.support.SepValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

/**
 * Main SEP gateway client used to request tokens, verify transactions, reverse transactions and
 * parse callback parameters.
 *
 * <p>Amounts in this SDK are sent as integer gateway amounts. SEP terminals commonly expect IRR.
 * If your business domain stores amounts in IRT (toman), convert to the unit expected by your
 * terminal before calling this client.
 *
 * <p>Network behavior is delegated to {@link SepHttpClient}: connect/read timeouts come from
 * {@link SepConfig}, and retries (when enabled) are applied only to transport errors, not to SEP
 * business response codes.
 *
 * <p>Thread-safety: this class is thread-safe for concurrent use after construction. It keeps
 * immutable references and does not mutate request-scoped state.
 *
 * <p>Example: requesting a token and building a redirect URL.
 *
 * <pre>{@code
 * TokenRequest request = TokenRequest.builder(12000, "ORDER-1001")
 *         .cellNumber("09120000000")
 *         .build();
 * TokenResult tokenResult = sepClient.requestToken(request);
 * String redirectUrl = sepClient.buildRedirectUrl(tokenResult.token());
 * }</pre>
 *
 * <p>Example: callback parsing and verification.
 *
 * <pre>{@code
 * SepCallback callback = sepClient.parseCallback(callbackParams);
 * if (callback.isOk()) {
 *     VerifyResult verifyResult = sepClient.verifyTransaction(new VerifyRequest(callback.refNum()));
 * }
 * }</pre>
 */
public final class SepClient {
    private static final String TOKEN_ACTION = "token";
    private static final int MAX_REDIRECT_URL_LENGTH = 2083;
    private static final int MAX_REDIRECT_URL_LENGTH_GET_METHOD = 1538;

    private final SepConfig config;
    private final SepHttpClient httpClient;

    /**
     * Creates a client using a default HTTP client built from the provided configuration.
     *
     * @param config validated SDK configuration
     * @throws SepValidationException when {@code config} is {@code null}
     */
    public SepClient(SepConfig config) {
        this(config, SepHttpClient.create(config));
    }

    /**
     * Creates a client with explicit configuration and HTTP transport implementation.
     *
     * @param config     validated SDK configuration
     * @param httpClient HTTP transport abstraction used for SEP calls
     * @throws SepValidationException when any required argument is {@code null}
     */
    public SepClient(SepConfig config, SepHttpClient httpClient) {
        if (config == null) {
            throw new SepValidationException("config is required");
        }
        if (httpClient == null) {
            throw new SepValidationException("httpClient is required");
        }
        this.config = config;
        this.httpClient = httpClient;
    }

    /**
     * Requests a payment token from SEP.
     *
     * <p>If {@link TokenRequest#redirectUrl()} is not provided, {@link SepConfig#callbackUrl()} is
     * used. Optional token expiry must be within configured min/max limits (no silent clamping).
     *
     * @param request token request payload
     * @return successful token response
     * @throws SepValidationException when the request is {@code null} or validation fails
     * @throws SepTransportException  when network communication with SEP fails
     * @throws SepApiException        when SEP returns an unsuccessful or malformed response
     */
    public TokenResult requestToken(TokenRequest request) {
        if (request == null) {
            throw new SepValidationException("token request is required");
        }
        validateTokenRequest(request);
        URI redirectUrl = request.redirectUrl() != null ? request.redirectUrl() : config.callbackUrl();
        String hashedCardNumber = normalizeHashedCardNumbers(request.hashedCardNumbers());
        Integer tokenExpiryInMin = normalizeTokenExpiry(request.tokenExpiryInMin());
        TokenPayload payload = new TokenPayload(
                TOKEN_ACTION,
                config.terminalId(),
                request.amount(),
                request.wage(),
                request.resNum(),
                redirectUrl,
                request.cellNumber(),
                tokenExpiryInMin,
                hashedCardNumber,
                request.getMethod(),
                request.resNum1(),
                request.resNum2(),
                request.resNum3(),
                request.resNum4(),
                request.tranType(),
                request.settlementIbanInfo()
        );
        return httpClient.post(SepEndpoints.token(), payload, TokenResult.class, SepResponseType.TOKEN, Set.of(1));
    }

    /**
     * Verifies a transaction by its SEP {@code refNum}.
     *
     * @param request verification request containing {@code refNum}
     * @return verification result payload
     * @throws SepValidationException when the request or {@code refNum} is invalid
     * @throws SepTransportException  when network communication with SEP fails
     * @throws SepApiException        when SEP returns an unsuccessful or malformed response
     */
    public VerifyResult verifyTransaction(VerifyRequest request) {
        if (request == null) {
            throw new SepValidationException("verify request is required");
        }
        validateTransactionRequest(request.refNum(), "refNum");
        VerifyPayload payload = new VerifyPayload(request.refNum(), parseTerminalNumber());
        return httpClient.post(SepEndpoints.verify(), payload, VerifyResult.class, SepResponseType.TRANSACTION, Set.of(0));
    }

    /**
     * Reverses a transaction by its SEP {@code refNum}.
     *
     * @param request reverse request containing {@code refNum}
     * @return reverse result payload
     * @throws SepValidationException when the request or {@code refNum} is invalid
     * @throws SepTransportException  when network communication with SEP fails
     * @throws SepApiException        when SEP returns an unsuccessful or malformed response
     */
    public ReverseResult reverseTransaction(ReverseRequest request) {
        if (request == null) {
            throw new SepValidationException("reverse request is required");
        }
        validateTransactionRequest(request.refNum(), "refNum");
        ReversePayload payload = new ReversePayload(request.refNum(), parseTerminalNumber());
        return httpClient.post(SepEndpoints.reverse(), payload, ReverseResult.class, SepResponseType.TRANSACTION, Set.of(0));
    }

    /**
     * Builds the gateway redirect URL for a previously issued token.
     *
     * <p>The returned URL targets SEP {@code /OnlinePG/SendToken} endpoint under configured
     * {@link SepConfig#baseUrl()}.
     *
     * @param token non-blank token value from {@link #requestToken(TokenRequest)}
     * @return full redirect URL to send the customer to SEP
     * @throws SepValidationException when {@code token} is blank
     */
    public String buildRedirectUrl(String token) {
        SepValidation.requireNonBlank(token, "token");
        return UriComponentsBuilder.fromUri(config.baseUrl())
                .path(SepEndpoints.sendToken())
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    /**
     * Parses callback parameters from a flat map.
     *
     * <p><strong>Security:</strong> this only parses gateway return parameters. It does
     * <em>not</em> prove payment success. Always call {@link #verifyTransaction(VerifyRequest)}
     * when {@link SepCallback#isOk()} is true, and reconcile {@code resNum}/amount with your
     * own order store before fulfilling.
     *
     * <p>Parameter names are matched case-insensitively. When both {@code State} and
     * {@code Status} are present they must map to the same semantic status, otherwise parsing
     * fails.
     *
     * @param params callback parameters, typically obtained from query/form data
     * @return parsed callback object
     * @throws SepCallbackException when callback payload is missing required fields or contains
     *                              invalid values
     */
    public SepCallback parseCallback(Map<String, String> params) {
        if (params == null) {
            throw new SepCallbackException("params is required");
        }
        String stateValue = findParam(params, "State");
        String statusValue = findParam(params, "Status");
        SepCallbackStatus statusFromState = SepCallbackStatus.fromState(stateValue);
        Integer statusCode = parseInteger(statusValue, "Status");
        SepCallbackStatus statusFromCode = statusCode != null ? SepCallbackStatus.fromCode(statusCode) : null;
        if (statusFromState != null && statusFromCode != null && statusFromState != statusFromCode) {
            throw new SepCallbackException("State and Status mismatch: " + stateValue + " / " + statusValue);
        }
        SepCallbackStatus status = statusFromState != null ? statusFromState : statusFromCode;
        if (status == null) {
            if (stateValue == null && statusValue == null) {
                throw new SepCallbackException("State or Status is required");
            }
            if (stateValue != null) {
                throw new SepCallbackException("State is invalid: " + stateValue);
            }
            throw new SepCallbackException("Status is invalid: " + statusValue);
        }
        SepCallback callback = new SepCallback(
                status,
                statusCode,
                findParam(params, "Token"),
                findParam(params, "ResNum"),
                findParam(params, "RefNum"),
                findParam(params, "TraceNo"),
                findParam(params, "TerminalId"),
                findParam(params, "MID"),
                findParam(params, "RRN"),
                parseLong(findParam(params, "Amount"), "Amount"),
                parseLong(findParam(params, "Wage"), "Wage"),
                parseLong(findParam(params, "AffectiveAmount"), "AffectiveAmount"),
                findParam(params, "SecurePan"),
                findParam(params, "HashedCardNumber")
        );
        if (callback.isOk() && (callback.refNum() == null || callback.refNum().isBlank())) {
            throw new SepCallbackException("RefNum is required when payment is OK");
        }
        return callback;
    }

    /**
     * Parses callback parameters from a multi-value map (for example Spring MVC form/query maps).
     *
     * <p>For keys with multiple values, only the first value is considered.
     *
     * @param params callback parameter map
     * @return parsed callback object
     * @throws SepCallbackException when callback payload is missing required fields or contains
     *                              invalid values
     */
    public SepCallback parseCallback(MultiValueMap<String, String> params) {
        if (params == null) {
            throw new SepCallbackException("params is required");
        }
        Map<String, String> flat = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                flat.put(entry.getKey(), entry.getValue().getFirst());
            }
        }
        return parseCallback(flat);
    }

    private void validateTokenRequest(TokenRequest request) {
        SepValidation.requirePositive(request.amount(), "amount");
        SepValidation.requireNonBlank(request.resNum(), "resNum");
        SepValidation.requireMaxLength(request.resNum(), config.maxResNumLength(), "resNum");
        URI redirectUrl = request.redirectUrl() != null ? request.redirectUrl() : config.callbackUrl();
        if (redirectUrl == null) {
            throw new SepValidationException("redirectUrl is required");
        }
        SepValidation.requireHttpUri(redirectUrl, "redirectUrl");
        int maxRedirectLength = Boolean.TRUE.equals(request.getMethod())
                ? MAX_REDIRECT_URL_LENGTH_GET_METHOD
                : MAX_REDIRECT_URL_LENGTH;
        SepValidation.requireMaxLength(redirectUrl.toString(), maxRedirectLength, "redirectUrl");
        if (request.cellNumber() != null) {
            SepValidation.requireNonBlank(request.cellNumber(), "cellNumber");
        }
        SepValidation.requireNonNegative(request.wage(), "wage");
        validateOptionalResNum(request.resNum1(), "resNum1");
        validateOptionalResNum(request.resNum2(), "resNum2");
        validateOptionalResNum(request.resNum3(), "resNum3");
        validateOptionalResNum(request.resNum4(), "resNum4");
        validateHashedCardNumbers(request.hashedCardNumbers());
        validateSettlementInfo(request.settlementIbanInfo(), request.tranType());
    }

    private void validateOptionalResNum(String value, String field) {
        if (value == null) {
            return;
        }
        SepValidation.requireNonBlank(value, field);
        SepValidation.requireMaxLength(value, config.maxResNumLength(), field);
    }

    private void validateHashedCardNumbers(List<String> hashedCardNumbers) {
        if (hashedCardNumbers == null) {
            return;
        }
        if (hashedCardNumbers.isEmpty()) {
            throw new SepValidationException("hashedCardNumbers must not be empty");
        }
        if (hashedCardNumbers.size() > config.maxHashedCardCount()) {
            throw new SepValidationException("hashedCardNumbers size must be at most " + config.maxHashedCardCount());
        }
        for (String value : hashedCardNumbers) {
            if (value == null || value.isBlank()) {
                throw new SepValidationException("hashedCardNumbers contains blank value");
            }
        }
    }

    private void validateSettlementInfo(List<SettlementIbanInfo> settlementIbanInfo, SepTranType tranType) {
        if (settlementIbanInfo == null) {
            if (tranType == SepTranType.GOVERNMENT) {
                throw new SepValidationException("settlementIbanInfo is required for Government transactions");
            }
            return;
        }
        if (settlementIbanInfo.isEmpty()) {
            throw new SepValidationException("settlementIbanInfo must not be empty");
        }
        if (settlementIbanInfo.size() > config.maxSettlementItems()) {
            throw new SepValidationException("settlementIbanInfo size must be at most " + config.maxSettlementItems());
        }
        for (SettlementIbanInfo info : settlementIbanInfo) {
            if (info == null) {
                throw new SepValidationException("settlementIbanInfo contains null");
            }
            SepValidation.requireIban(info.iban());
            SepValidation.requirePositive(info.amount(), "settlementIbanInfo.amount");
            SepValidation.requireNonBlank(info.purchaseId(), "settlementIbanInfo.purchaseId");
        }
    }

    private void validateTransactionRequest(String refNum, String field) {
        SepValidation.requireNonBlank(refNum, field);
    }

    private String normalizeHashedCardNumbers(List<String> hashedCardNumbers) {
        if (hashedCardNumbers == null) {
            return null;
        }
        List<String> normalized = new ArrayList<>();
        for (String value : hashedCardNumbers) {
            if (value != null) {
                String trimmed = value.trim();
                if (!trimmed.isBlank()) {
                    normalized.add(trimmed);
                }
            }
        }
        if (normalized.isEmpty()) {
            return null;
        }
        return String.join("|", normalized);
    }

    private Integer normalizeTokenExpiry(Integer tokenExpiryInMin) {
        if (tokenExpiryInMin == null) {
            return null;
        }
        int min = config.minTokenExpiryInMin();
        int max = config.maxTokenExpiryInMin();
        if (tokenExpiryInMin < min || tokenExpiryInMin > max) {
            throw new SepValidationException(
                    "tokenExpiryInMin must be between " + min + " and " + max + " (inclusive)"
            );
        }
        return tokenExpiryInMin;
    }

    private long parseTerminalNumber() {
        try {
            return Long.parseLong(config.terminalId());
        } catch (NumberFormatException ex) {
            throw new SepValidationException("terminalId must be numeric", ex);
        }
    }

    private Integer parseInteger(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new SepCallbackException(field + " is invalid: " + value);
        }
    }

    private Long parseLong(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new SepCallbackException(field + " is invalid: " + value);
        }
    }

    private String findParam(Map<String, String> params, String name) {
        if (params.containsKey(name)) {
            return params.get(name);
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private record TokenPayload(
            @JsonProperty("action") String action,
            String terminalId,
            long amount,
            Long wage,
            String resNum,
            URI redirectUrl,
            String cellNumber,
            Integer tokenExpiryInMin,
            String hashedCardNumber,
            Boolean getMethod,
            String resNum1,
            String resNum2,
            String resNum3,
            String resNum4,
            SepTranType tranType,
            List<SettlementIbanInfo> settlementIbanInfo
    ) {
    }

    private record VerifyPayload(String refNum, long terminalNumber) {
    }

    private record ReversePayload(String refNum, long terminalNumber) {
    }
}
