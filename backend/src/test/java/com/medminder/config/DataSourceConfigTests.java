package com.medminder.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class DataSourceConfigTests {

    @Test
    void usesCredentialsParsedFromRenderUrlWhenExplicitValuesAreNotProvided() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.username", "medminder")
                .withProperty("spring.datasource.password", "medminder")
                .withProperty(
                        "DATABASE_URL",
                        "postgresql://medminder_user:secret123@dpg-example.singapore-postgres.render.com/medminder");

        HikariDataSource dataSource = (HikariDataSource) new DataSourceConfig().dataSource(environment);

        assertThat(dataSource.getJdbcUrl())
                .isEqualTo(
                        "jdbc:postgresql://dpg-example.singapore-postgres.render.com:5432/medminder");
        assertThat(dataSource.getUsername()).isEqualTo("medminder_user");
        assertThat(dataSource.getPassword()).isEqualTo("secret123");

        dataSource.close();
    }
}
