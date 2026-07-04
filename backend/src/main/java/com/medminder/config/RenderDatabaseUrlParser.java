package com.medminder.config;

import java.net.URI;

public final class RenderDatabaseUrlParser {

    private RenderDatabaseUrlParser() {}

    public static DatabaseConnectionSettings parse(String databaseUrl) {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalArgumentException("databaseUrl must not be blank");
        }

        String normalizedUrl = databaseUrl.trim();

        if (normalizedUrl.startsWith("jdbc:")) {
            return new DatabaseConnectionSettings(normalizedUrl, null, null);
        }

        URI uri = URI.create(normalizedUrl);
        String scheme = uri.getScheme();
        if (!"postgres".equalsIgnoreCase(scheme) && !"postgresql".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Unsupported database URL scheme: " + scheme);
        }

        String userInfo = uri.getUserInfo();
        String username = null;
        String password = null;
        if (userInfo != null && !userInfo.isBlank()) {
            String[] credentials = userInfo.split(":", 2);
            username = credentials[0];
            if (credentials.length > 1) {
                password = credentials[1];
            }
        }

        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String databaseName = uri.getPath() == null ? "" : uri.getPath();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + databaseName;
        if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
            jdbcUrl += "?" + uri.getQuery();
        }

        return new DatabaseConnectionSettings(jdbcUrl, username, password);
    }

    public record DatabaseConnectionSettings(String jdbcUrl, String username, String password) {}
}
