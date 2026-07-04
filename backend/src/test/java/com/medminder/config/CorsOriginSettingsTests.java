package com.medminder.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CorsOriginSettingsTests {

    @Test
    void usesLocalDefaultsWhenNoOriginOverrideIsProvided() {
        assertIterableEquals(
            List.of("http://localhost:5173", "http://127.0.0.1:5173"),
            CorsOriginSettings.resolveAllowedOrigins(null)
        );
    }

    @Test
    void splitsCommaSeparatedOriginsAndTrimsWhitespace() {
        assertEquals(
            List.of("https://medminder.example.com", "https://medminder-admin.example.com"),
            CorsOriginSettings.resolveAllowedOrigins(" https://medminder.example.com , https://medminder-admin.example.com ")
        );
    }
}
