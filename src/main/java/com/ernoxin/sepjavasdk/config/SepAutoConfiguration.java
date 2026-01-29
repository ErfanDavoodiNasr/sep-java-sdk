package com.ernoxin.sepjavasdk.config;

import com.ernoxin.sepjavasdk.client.SepClient;
import com.ernoxin.sepjavasdk.http.SepHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(SepProperties.class)
public class SepAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public SepConfig sepConfig(SepProperties properties) {
        return properties.toConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public SepHttpClient sepHttpClient(SepConfig config) {
        return SepHttpClient.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public SepClient sepClient(SepConfig config, SepHttpClient httpClient) {
        return new SepClient(config, httpClient);
    }
}
