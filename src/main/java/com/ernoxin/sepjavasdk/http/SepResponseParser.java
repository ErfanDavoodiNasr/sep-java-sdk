package com.ernoxin.sepjavasdk.http;

import com.ernoxin.sepjavasdk.exception.SepApiException;
import com.ernoxin.sepjavasdk.support.SepErrorCatalog;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public final class SepResponseParser {
    private final ObjectMapper mapper;

    public SepResponseParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T parse(ResponseEntity<String> response, SepResponseType responseType, Set<Integer> successCodes, Class<T> dataType) {
        int httpStatus = response.getStatusCode().value();
        String body = response.getBody();
        if (body == null || body.isBlank()) {
            throw new SepApiException(httpStatus, null, "Empty response body", body);
        }
        JsonNode root = readTree(httpStatus, body);
        switch (responseType) {
            case TOKEN -> validateTokenResponse(httpStatus, root, body, successCodes, response);
            case TRANSACTION -> validateTransactionResponse(httpStatus, root, body, successCodes, response);
            default -> throw new SepApiException(httpStatus, null, "Unsupported response type", body);
        }
        try {
            return mapper.treeToValue(root, dataType);
        } catch (JacksonException ex) {
            throw new SepApiException(httpStatus, null, "Invalid response body", body, ex);
        }
    }

    private void validateTokenResponse(int httpStatus, JsonNode root, String body, Set<Integer> successCodes, ResponseEntity<String> response) {
        Integer status = extractCode(root.get("status"));
        if (status == null) {
            throw new SepApiException(httpStatus, null, "Missing status in response", body);
        }
        if (!successCodes.contains(status)) {
            Integer errorCode = extractCode(root.get("errorCode"));
            String errorDesc = textOrNull(root.get("errorDesc"));
            Integer code = errorCode != null ? errorCode : status;
            String message = resolveTokenMessage(code, errorDesc);
            throw new SepApiException(httpStatus, code, message, body);
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new SepApiException(httpStatus, status, "Non-success HTTP response", body);
        }
    }

    private void validateTransactionResponse(int httpStatus, JsonNode root, String body, Set<Integer> successCodes, ResponseEntity<String> response) {
        Integer resultCode = extractCode(root.get("ResultCode"));
        Boolean success = booleanOrNull(root.get("Success"));
        String description = textOrNull(root.get("ResultDescription"));
        if (resultCode == null) {
            throw new SepApiException(httpStatus, null, "Missing ResultCode in response", body);
        }
        if (!successCodes.contains(resultCode) || success == null || !success) {
            String message = resolveTransactionMessage(resultCode, description);
            throw new SepApiException(httpStatus, resultCode, message, body);
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new SepApiException(httpStatus, resultCode, "Non-success HTTP response", body);
        }
    }

    private JsonNode readTree(int status, String body) {
        try {
            return mapper.readTree(body);
        } catch (JacksonException ex) {
            throw new SepApiException(status, null, "Invalid JSON response", body, ex);
        }
    }

    private Integer extractCode(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isInt() || node.isLong()) {
            return node.asInt();
        }
        if (node.isTextual()) {
            String text = node.asText();
            if (text != null) {
                String trimmed = text.trim();
                try {
                    return Integer.parseInt(trimmed);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    private Boolean booleanOrNull(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isTextual()) {
            String text = node.asText();
            if (text != null) {
                String trimmed = text.trim();
                if (trimmed.equalsIgnoreCase("true")) {
                    return true;
                }
                if (trimmed.equalsIgnoreCase("false")) {
                    return false;
                }
            }
        }
        return null;
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isTextual()) {
            String value = node.asText();
            return value != null && !value.isBlank() ? value : null;
        }
        return null;
    }

    private String resolveTokenMessage(Integer code, String fallback) {
        String catalogMessage = SepErrorCatalog.messageForToken(code);
        if (catalogMessage != null && !catalogMessage.isBlank()) {
            return catalogMessage;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "SEP token request failed";
    }

    private String resolveTransactionMessage(Integer code, String fallback) {
        String catalogMessage = SepErrorCatalog.messageForTransaction(code);
        if (catalogMessage != null && !catalogMessage.isBlank()) {
            return catalogMessage;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "SEP transaction error";
    }
}
