package com.demo.demo;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class Constants {

    public static final String LOGIN_URL = "/login";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String ISSUER_INFO = "DemoApp";
    public static final long TOKEN_EXPIRATION_TIME = 86400000;

    public static Key getSigningKeyB64(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Key getSigningKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Constants() {
        // Utility class
    }
}
