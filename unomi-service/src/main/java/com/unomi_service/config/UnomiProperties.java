package com.unomi_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "unomi")
public record UnomiProperties(
        String baseUrl,
        String username,
        String password,
        String scope,
        String sourceItemId
) {
    public UnomiProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8181";
        }
        if (username == null || username.isBlank()) {
            username = "karaf";
        }
        if (password == null || password.isBlank()) {
            password = "karaf";
        }
        if (scope == null || scope.isBlank()) {
            scope = "myweb";
        }
        if (sourceItemId == null || sourceItemId.isBlank()) {
            sourceItemId = "my-ecommerce-site";
        }
    }
}