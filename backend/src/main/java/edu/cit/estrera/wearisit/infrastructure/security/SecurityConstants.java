package edu.cit.estrera.wearisit.infrastructure.security;


public class SecurityConstants {

    public static final String[] WHITELIST = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/google/**",
            "/oauth2/**",
            "/login/**"
    };

    public static final String[] WHITELIST_EXACT = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
    };

    public static final String[] WHITELIST_PREFIX = {
            "/api/auth/google/",
            "/oauth2/",
            "/login/"
    };
}