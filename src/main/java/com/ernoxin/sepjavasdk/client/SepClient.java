package com.ernoxin.sepjavasdk.client;

import com.ernoxin.sepjavasdk.callback.SepCallback;
import com.ernoxin.sepjavasdk.callback.SepCallbackStatus;
import com.ernoxin.sepjavasdk.config.SepConfig;
import com.ernoxin.sepjavasdk.exception.SepCallbackException;
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

public final class SepClient {
    private static final String TOKEN_ACTION = "token";

    private final SepConfig config;
    private final SepHttpClient httpClient;

    public SepClient(SepConfig config) {
        this(config, SepHttpClient.create(config));
    }

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

    public VerifyResult verifyTransaction(VerifyRequest request) {
        if (request == null) {
            throw new SepValidationException("verify request is required");
        }
        validateTransactionRequest(request.refNum(), "refNum");
        VerifyPayload payload = new VerifyPayload(request.refNum(), parseTerminalNumber());
        return httpClient.post(SepEndpoints.verify(), payload, VerifyResult.class, SepResponseType.TRANSACTION, Set.of(0));
    }

    public ReverseResult reverseTransaction(ReverseRequest request) {
        if (request == null) {
            throw new SepValidationException("reverse request is required");
        }
        validateTransactionRequest(request.refNum(), "refNum");
        ReversePayload payload = new ReversePayload(request.refNum(), parseTerminalNumber());
        return httpClient.post(SepEndpoints.reverse(), payload, ReverseResult.class, SepResponseType.TRANSACTION, Set.of(0));
    }

    public String buildRedirectUrl(String token) {
        SepValidation.requireNonBlank(token, "token");
        return UriComponentsBuilder.fromUri(config.baseUrl())
                .path(SepEndpoints.sendToken())
                .queryParam("token", token)
                .build()
                .toUriString();
    }

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
        if (tokenExpiryInMin < min) {
            return min;
        }
        if (tokenExpiryInMin > max) {
            return max;
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
