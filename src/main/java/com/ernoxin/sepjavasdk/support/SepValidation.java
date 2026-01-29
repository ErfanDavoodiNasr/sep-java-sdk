package com.ernoxin.sepjavasdk.support;

import com.ernoxin.sepjavasdk.exception.SepValidationException;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

@UtilityClass
public class SepValidation {
    private static final Pattern TERMINAL_ID_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern IBAN_PATTERN = Pattern.compile("^IR\\d{24}$");

    public static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new SepValidationException(field + " is required");
        }
    }

    public static void requirePositive(long value, String field) {
        if (value <= 0) {
            throw new SepValidationException(field + " must be positive");
        }
    }

    public static void requireMin(long value, long min, String field) {
        if (value < min) {
            throw new SepValidationException(field + " must be at least " + min);
        }
    }

    public static void requireNonNegative(Long value, String field) {
        if (value == null) {
            return;
        }
        if (value < 0) {
            throw new SepValidationException(field + " must be non-negative");
        }
    }

    public static void requireMaxLength(String value, int max, String field) {
        if (value != null && value.length() > max) {
            throw new SepValidationException(field + " must be at most " + max + " characters");
        }
    }

    public static void requireHttpUri(URI uri, String field) {
        if (uri == null) {
            throw new SepValidationException(field + " is required");
        }
        if (!uri.isAbsolute() || uri.getScheme() == null) {
            throw new SepValidationException(field + " must be an absolute URL");
        }
        String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new SepValidationException(field + " must use http or https");
        }
    }

    public static void requireTerminalId(String terminalId) {
        requireNonBlank(terminalId, "terminalId");
        String trimmed = terminalId.trim();
        if (!TERMINAL_ID_PATTERN.matcher(trimmed).matches()) {
            throw new SepValidationException("terminalId must be numeric");
        }
    }

    public static void requireIban(String iban) {
        requireNonBlank(iban, "settlementIbanInfo.iban");
        String normalized = iban.trim().toUpperCase(Locale.ROOT);
        if (!IBAN_PATTERN.matcher(normalized).matches()) {
            throw new SepValidationException("settlementIbanInfo.iban must start with IR and be 26 characters");
        }
    }

    public static URI normalizeBaseUrl(URI uri) {
        String value = uri.toString();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return URI.create(value);
    }
}
