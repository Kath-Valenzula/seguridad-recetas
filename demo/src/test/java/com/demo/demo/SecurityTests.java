package com.demo.demo;

import java.io.IOException;
import java.security.Key;
import java.util.Objects;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SecurityTests {

    private static final String TEST_JWT_SECRET = "test-secret-change-me-test-secret-change-me";

    @Test
    void testConstants() {
        assertEquals("/login", Constants.LOGIN_URL);
        assertEquals("Authorization", Constants.HEADER_AUTHORIZATION);
        assertEquals("Bearer ", Constants.BEARER_PREFIX);
        assertEquals("DemoApp", Constants.ISSUER_INFO);
        assertEquals(86400000, Constants.TOKEN_EXPIRATION_TIME);

        Key key = Constants.getSigningKey(TEST_JWT_SECRET);
        assertNotNull(key);

        // Just to cover the method, even if not used in app
        Key keyB64 = Constants.getSigningKeyB64("b8d5f49a3e2c9d6f1a0e7b5c2d3a9e8f1234567890abcdef");
        assertNotNull(keyB64);
    }

    @Test
    void testJWTAuthenticationConfig() {
        JWTAuthenticationConfig config = new JWTAuthenticationConfig(TEST_JWT_SECRET);
        String token = config.getJWTToken("juan");
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    void testJwtAuthenticationEntryPoint() throws IOException {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = mock(AuthenticationException.class);

        entryPoint.commence(request, response, authException);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: No autorizado o token inválido", Objects.requireNonNull(response.getErrorMessage()));
    }

    @Test
    void testJWTAuthorizationFilterValidToken() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        JWTAuthenticationConfig config = new JWTAuthenticationConfig(TEST_JWT_SECRET);
        String token = config.getJWTToken("juan");

        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(TEST_JWT_SECRET);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.HEADER_AUTHORIZATION, token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("juan", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testJWTAuthorizationFilterNoToken() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(TEST_JWT_SECRET);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testJWTAuthorizationFilterInvalidToken() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(TEST_JWT_SECRET);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.HEADER_AUTHORIZATION, "Bearer invalidtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    void testConstantsConstructor() throws Exception {
        var constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }

    @Test
    void testJWTAuthorizationFilterExpiredToken() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(TEST_JWT_SECRET);
        String token = io.jsonwebtoken.Jwts.builder()
                .subject("juan")
                .issuedAt(new java.util.Date(System.currentTimeMillis() - 10000))
                .expiration(new java.util.Date(System.currentTimeMillis() - 5000))
            .signWith(Constants.getSigningKey(TEST_JWT_SECRET))
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.HEADER_AUTHORIZATION, Constants.BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    void testJWTAuthorizationFilterNoAuthorities() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(TEST_JWT_SECRET);
        String token = io.jsonwebtoken.Jwts.builder()
                .subject("juan")
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .expiration(new java.util.Date(System.currentTimeMillis() + 10000))
            .signWith(Constants.getSigningKey(TEST_JWT_SECRET))
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.HEADER_AUTHORIZATION, Constants.BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        org.junit.jupiter.api.Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testJWTAuthorizationFilterNonBearerToken() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(TEST_JWT_SECRET);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.HEADER_AUTHORIZATION, "Basic sometoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        org.junit.jupiter.api.Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDemoApplication() {
        assertDoesNotThrow(() -> DemoApplication.main(new String[] {}));
    }
}
