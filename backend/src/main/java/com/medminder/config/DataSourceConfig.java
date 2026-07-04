package com.medminder.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(Environment environment) {
        String rawUrl = firstNonBlank(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("DATABASE_URL"));

        String resolvedUrl = rawUrl;
        String username = firstNonBlank(
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("DB_USERNAME"),
                environment.getProperty("DB_USER"));
        String password = firstNonBlank(
                environment.getProperty("spring.datasource.password"),
                environment.getProperty("DB_PASSWORD"),
                environment.getProperty("DB_PASS"));

        if (rawUrl != null && !rawUrl.isBlank()) {
            RenderDatabaseUrlParser.DatabaseConnectionSettings settings =
                    RenderDatabaseUrlParser.parse(rawUrl);
            resolvedUrl = settings.jdbcUrl();
            if (!rawUrl.startsWith("jdbc:")) {
                username = firstNonBlank(settings.username(), username);
                password = firstNonBlank(settings.password(), password);
            } else {
                username = firstNonBlank(username, settings.username());
                password = firstNonBlank(password, settings.password());
            }
        } else {
            String host = environment.getProperty("DB_HOST", "localhost");
            String port = environment.getProperty("DB_PORT", "5432");
            String databaseName = environment.getProperty("DB_NAME", "medminder");
            resolvedUrl = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
        }

        String driverClassName =
                firstNonBlank(environment.getProperty("spring.datasource.driver-class-name"), inferDriverClassName(resolvedUrl));

        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(resolvedUrl)
                .username(username)
                .password(password)
                .build();
    }

    private static String inferDriverClassName(String jdbcUrl) {
        if (jdbcUrl != null && jdbcUrl.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        return "org.postgresql.Driver";
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
