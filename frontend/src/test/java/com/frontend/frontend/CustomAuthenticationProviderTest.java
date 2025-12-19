package com.frontend.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private TokenStore tokenStore;

    @Mock
    private RestTemplate restTemplate;

    private CustomAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        provider = new CustomAuthenticationProvider(tokenStore, restTemplate);
    }

    @Test
    void authenticate_success_setsTokenAndReturnsAuth() {
        var response = new ResponseEntity<>("{\"token\":\"abc123\"}", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(response);

        Authentication result = provider.authenticate(
                new UsernamePasswordAuthenticationToken("user", "pass"));

        assertNotNull(result);
        assertEquals("user", result.getName());
        assertEquals("pass", result.getCredentials());
        assertFalse(result.getAuthorities().isEmpty());
        verify(tokenStore).setToken("abc123");
    }
}
