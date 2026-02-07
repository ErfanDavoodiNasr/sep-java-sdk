package com.ernoxin.sepjavasdk.support;

import com.ernoxin.sepjavasdk.exception.SepValidationException;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Validation helpers used by SEP SDK for fail-fast input checks.
 */
@UtilityClass
public class SepValidation {
    private static final Pattern TERMINAL_ID_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern IBAN_PATTERN = Pattern.compile("^IR\\d{24}$");

    /**
     * Ensures a value is not {@code null} or blank.
     *
     * @param value value to validate
     * @param field logical field name for error messages
     * @throws SepValidationException when validation fails
     */
    public static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new SepValidationException(field + " is required");
        }
    }

    /**
     * Ensures a numeric value is positive ({@code > 0}).
     *
     * @param value value to validate
     * @param field logical field name for error messages
     * @throws SepValidationException when validation fails
     */
    public static void requirePositive(long value, String field) {
        if (value <= 0) {
            throw new SepValidationException(field + " must be positive");
        }
    }

    /**
     * Ensures a numeric value is greater than or equal to minimum.
     *
     * @param value value to validate
     * @param min inclusive minimum
     * @param field logical field name for error messages
     * @throws SepValidationException when validation fails
     */
    public static void requireMin(long value, long min, String field) {
        if (value < min) {
            throw new SepValidationException(field + " must be at least " + min);
        }
    }

    /**
     * Ensures optional numeric value is non-negative.
     *
     * @param value value to validate; ignored when {@code null}
     * @param field logical field name for error messages
     * @throws SepValidationException when validation fails
     */
    public static void requireNonNegative(Long value, String field) {
        if (value == null) {
            return;
        }
        if (value < 0) {
            throw new SepValidationException(field + " must be non-negative");
        }
    }

    /**
     * Ensures optional string length does not exceed maximum.
     *
     * @param value value to validate; ignored when {@code null}
     * @param max maximum allowed length
     * @param field logical field name for error messages
     * @throws SepValidationException when validation fails
     */
    public static void requireMaxLength(String value, int max, String field) {
        if (value != null && value.length() > max) {
            throw new SepValidationException(field + " must be at most " + max + " characters");
        }
    }

    /**
     * Ensures URI is absolute HTTP/HTTPS.
     *
     * @param uri URI to validate
     * @param field logical field name for error messages
     * @throws SepValidationException when validation fails
     */
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

    /**
     * Ensures terminal id is numeric and non-blank.
     *
     * @param terminalId terminal id value
     * @throws SepValidationException when validation fails
     */
    public static void requireTerminalId(String terminalId) {
        requireNonBlank(terminalId, "terminalId");
        String trimmed = terminalId.trim();
        if (!TERMINAL_ID_PATTERN.matcher(trimmed).matches()) {
            throw new SepValidationException("terminalId must be numeric");
        }
    }

    /**
     * Ensures IBAN matches expected SEP format ({@code IR} + 24 digits).
     *
     * @param iban IBAN value
     * @throws SepValidationException when validation fails
     */
    public static void requireIban(String iban) {
        requireNonBlank(iban, "settlementIbanInfo.iban");
        String normalized = iban.trim().toUpperCase(Locale.ROOT);
        if (!IBAN_PATTERN.matcher(normalized).matches()) {
            throw new SepValidationException("settlementIbanInfo.iban must start with IR and be 26 characters");
        }
    }

    /**
     * Removes trailing slash characters from a base URI.
     *
     * @param uri base URI
     * @return normalized URI without trailing slash
     */
    public static URI normalizeBaseUrl(URI uri) {
        String value = uri.toString();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return URI.create(value);
    }
}
