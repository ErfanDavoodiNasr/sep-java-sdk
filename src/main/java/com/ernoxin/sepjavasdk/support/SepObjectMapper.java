package com.ernoxin.sepjavasdk.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;

/**
 * Factory for SDK-specific Jackson {@link ObjectMapper}.
 */
@UtilityClass
public class SepObjectMapper {
    /**
     * Creates object mapper configured for SEP payload conventions.
     *
     * <p>Configuration includes:
     * <ul>
     * <li>UpperCamel JSON property naming.</li>
     * <li>Ignoring unknown properties.</li>
     * <li>Case-insensitive enum values.</li>
     * <li>Excluding {@code null} values from serialization.</li>
     * </ul>
     *
     * @return configured object mapper
     */
    public static ObjectMapper create() {
        return JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .build();
    }
}
