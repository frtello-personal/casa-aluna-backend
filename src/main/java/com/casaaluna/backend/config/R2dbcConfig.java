package com.casaaluna.backend.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * R2DBC Configuration for reactive PostgreSQL database access
 * 
 * Configures:
 * - Custom converters for Java types to PostgreSQL types
 * - R2DBC repositories
 * - Connection factory settings
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "com.casaaluna.backend.repository")
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private final ConnectionFactory connectionFactory;

    public R2dbcConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    /**
     * Custom converters for R2DBC to handle Java types properly
     */
    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new InstantToLongConverter());
        converters.add(new LongToInstantConverter());
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }

    /**
     * Convert Instant to Long (epoch milliseconds) for database storage
     */
    static class InstantToLongConverter implements Converter<Instant, Long> {
        @Override
        public Long convert(Instant source) {
            return source.toEpochMilli();
        }
    }

    /**
     * Convert Long (epoch milliseconds) to Instant when reading from database
     */
    static class LongToInstantConverter implements Converter<Long, Instant> {
        @Override
        public Instant convert(Long source) {
            return Instant.ofEpochMilli(source);
        }
    }
}
