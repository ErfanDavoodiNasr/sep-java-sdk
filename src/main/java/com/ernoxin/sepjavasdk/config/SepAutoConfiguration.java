package com.ernoxin.sepjavasdk.config;

import com.ernoxin.sepjavasdk.client.SepClient;
import com.ernoxin.sepjavasdk.http.SepHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for SEP SDK beans.
 *
 * <p>Registers {@link SepConfig}, {@link SepHttpClient}, and {@link SepClient} when no custom
 * beans of the same types are provided by the application.
 *
 * <p>Opt-in: set {@code sep.enabled=true}. Without that flag, no beans are registered.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "sep", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SepProperties.class)
public class SepAutoConfiguration {
    /**
     * Creates immutable SDK configuration from bound properties.
     *
     * @param properties bound Spring Boot properties
     * @return validated runtime configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public SepConfig sepConfig(SepProperties properties) {
        return properties.toConfig();
    }

    /**
     * Creates default SEP HTTP client.
     *
     * @param config SDK configuration
     * @return HTTP client used by {@link SepClient}
     */
    @Bean
    @ConditionalOnMissingBean
    public SepHttpClient sepHttpClient(SepConfig config) {
        return SepHttpClient.create(config);
    }

    /**
     * Creates primary SEP client bean.
     *
     * @param config     SDK configuration
     * @param httpClient HTTP client implementation
     * @return fully configured SEP client
     */
    @Bean
    @ConditionalOnMissingBean
    public SepClient sepClient(SepConfig config, SepHttpClient httpClient) {
        return new SepClient(config, httpClient);
    }
}
