package com.medminder.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RenderDatabaseUrlParserTests {

    @Test
    void parsesRenderStylePostgresUrlIntoJdbcSettings() {
        String databaseUrl =
                "postgresql://medminder_user:secret123@dpg-example.singapore-postgres.render.com/medminder";

        RenderDatabaseUrlParser.DatabaseConnectionSettings settings =
                RenderDatabaseUrlParser.parse(databaseUrl);

        assertThat(settings.jdbcUrl())
                .isEqualTo(
                        "jdbc:postgresql://dpg-example.singapore-postgres.render.com:5432/medminder");
        assertThat(settings.username()).isEqualTo("medminder_user");
        assertThat(settings.password()).isEqualTo("secret123");
    }

    @Test
    void keepsJdbcUrlUntouchedWhenAlreadyJdbc() {
        String databaseUrl =
                "jdbc:postgresql://dpg-example.singapore-postgres.render.com:5432/medminder";

        RenderDatabaseUrlParser.DatabaseConnectionSettings settings =
                RenderDatabaseUrlParser.parse(databaseUrl);

        assertThat(settings.jdbcUrl()).isEqualTo(databaseUrl);
        assertThat(settings.username()).isNull();
        assertThat(settings.password()).isNull();
    }

    @Test
    void trimsWhitespaceAroundDatabaseUrl() {
        String databaseUrl =
                "  postgresql://medminder_user:secret123@dpg-example.singapore-postgres.render.com/medminder  ";

        RenderDatabaseUrlParser.DatabaseConnectionSettings settings =
                RenderDatabaseUrlParser.parse(databaseUrl);

        assertThat(settings.jdbcUrl())
                .isEqualTo(
                        "jdbc:postgresql://dpg-example.singapore-postgres.render.com:5432/medminder");
        assertThat(settings.username()).isEqualTo("medminder_user");
        assertThat(settings.password()).isEqualTo("secret123");
    }
}
