package com.medminder.config;

import java.util.Arrays;
import java.util.List;

public final class CorsOriginSettings {

    private CorsOriginSettings() {}

    public static List<String> resolveAllowedOrigins(String rawOrigins) {
        if (rawOrigins == null || rawOrigins.isBlank()) {
            return List.of("http://localhost:5173", "http://127.0.0.1:5173");
        }

        return Arrays.stream(rawOrigins.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
    }
}
