package com.ernoxin.sepjavasdk.http;

import com.ernoxin.sepjavasdk.config.SepConfig;
import com.ernoxin.sepjavasdk.exception.SepTransportException;
import com.ernoxin.sepjavasdk.exception.SepValidationException;
import com.ernoxin.sepjavasdk.support.SepObjectMapper;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Low-level HTTP client for SEP API communication.
 *
 * <p>This type serializes requests, sends POST calls through Spring {@link RestTemplate}, and
 * delegates response validation/parsing to {@link SepResponseParser}.
 *
 * <p>Timeout and retry behavior:
 * <ul>
 * <li>Connect/read timeouts are read from {@link SepConfig}.</li>
 * <li>Retries are attempted only for {@link RestClientException} transport failures.</li>
 * <li>SEP business errors are not retried.</li>
 * </ul>
 *
 * <p>Thread-safety: this class is thread-safe for concurrent use after construction.
 */
public final class SepHttpClient {
    private static final ResponseExtractor<ResponseEntity<String>> responseExtractor = response -> {
        String responseBody = null;
        try (InputStream stream = response.getBody()) {
            if (stream != null) {
                responseBody = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
            }
        }
        return new ResponseEntity<>(responseBody, response.getHeaders(), response.getStatusCode());
    };
    private final SepConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final SepResponseParser responseParser;

    /**
     * Creates a client with explicit transport and mapper dependencies.
     *
     * @param config       SDK configuration
     * @param restTemplate REST template instance used for calls
     * @param mapper       object mapper for request/response JSON
     */
    public SepHttpClient(SepConfig config, RestTemplate restTemplate, ObjectMapper mapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.responseParser = new SepResponseParser(mapper);
        configureRestTemplate(restTemplate, config);
    }

    /**
     * Creates a default HTTP client using JDK {@link HttpClient} backend and SDK JSON mapper.
     *
     * @param config SDK configuration
     * @return configured HTTP client
     */
    public static SepHttpClient create(SepConfig config) {
        ObjectMapper mapper = SepObjectMapper.create();
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(config.connectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new SepHttpClient(config, restTemplate, mapper);
    }

    /**
     * Sends a JSON POST request to SEP and parses a typed response.
     *
     * @param path SEP endpoint path appended to configured base URL
     * @param request request payload object
     * @param dataType expected response data type
     * @param responseType response validation strategy
     * @param successCodes logical gateway success codes for selected response type
     * @param <T> result type
     * @return parsed successful response payload
     * @throws SepValidationException when request serialization fails
     * @throws SepTransportException when transport fails after retries
     */

    private static void configureRestTemplate(RestTemplate restTemplate, SepConfig config) {
        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
        if (requestFactory instanceof SimpleClientHttpRequestFactory simpleFactory) {
            simpleFactory.setConnectTimeout((int) config.connectTimeout().toMillis());
            simpleFactory.setReadTimeout((int) config.readTimeout().toMillis());
        } else if (requestFactory instanceof JdkClientHttpRequestFactory jdkFactory) {
            jdkFactory.setReadTimeout(config.readTimeout());
        }
        if (restTemplate.getErrorHandler() instanceof DefaultResponseErrorHandler) {
            // SEP may return business failure payloads with non-2xx statuses; let parser inspect body.
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(@NonNull ClientHttpResponse response) {
                    return false;
                }

                @Override
                public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) {
                }
            });
        }
    }

    /**
     * Sends a mutating JSON POST that is never retried (token/verify/reverse).
     *
     * @see #post(String, Object, Class, SepResponseType, Set, boolean)
     */
    public <T> T post(String path, Object request, Class<T> dataType, SepResponseType responseType, Set<Integer> successCodes) {
        return post(path, request, dataType, responseType, successCodes, false);
    }

    /**
     * Sends a JSON POST request to SEP and parses a typed response.
     *
     * <p>Retries apply only when {@code retryable} is {@code true} and retry is enabled in config.
     * Token, verify, and reverse must keep {@code retryable=false}.
     *
     * @param path         SEP endpoint path appended to configured base URL
     * @param request      request payload object
     * @param dataType     expected response data type
     * @param responseType response validation strategy
     * @param successCodes logical gateway success codes for selected response type
     * @param retryable    whether transport retries are allowed for this call
     * @param <T>          result type
     * @return parsed successful response payload
     */
    public <T> T post(
            String path,
            Object request,
            Class<T> dataType,
            SepResponseType responseType,
            Set<Integer> successCodes,
            boolean retryable
    ) {
        URI baseUrl = config.baseUrl();
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(path).build().toUri();
        String body = writeBody(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.USER_AGENT, config.userAgent());
        int attempts = (retryable && config.retryEnabled()) ? config.retryMaxAttempts() : 1;
        long backoffMillis = (retryable && config.retryEnabled()) ? config.retryBackoff().toMillis() : 0;
        RestClientException last = null;
        for (int attempt = 1; attempt <= attempts; attempt++) {
            ResponseEntity<String> response;
            try {
                response = restTemplate.execute(url, HttpMethod.POST, httpRequest -> {
                    httpRequest.getHeaders().putAll(headers);
                    if (body != null && !body.isBlank()) {
                        httpRequest.getBody().write(body.getBytes(StandardCharsets.UTF_8));
                    }
                }, responseExtractor);
            } catch (RestClientException ex) {
                last = ex;
                if (attempt == attempts) {
                    throw new SepTransportException("Request to SEP failed", ex);
                }
                if (backoffMillis > 0) {
                    try {
                        Thread.sleep(backoffMillis);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new SepTransportException("Request to SEP failed", interrupted);
                    }
                }
                continue;
            }
            if (response == null) {
                throw new SepTransportException("Request to SEP failed", null);
            }
            return responseParser.parse(response, responseType, successCodes, dataType);
        }
        throw new SepTransportException("Request to SEP failed", last);
    }

    private String writeBody(Object request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (JacksonException ex) {
            throw new SepValidationException("Request body is invalid", ex);
        }
    }
}
