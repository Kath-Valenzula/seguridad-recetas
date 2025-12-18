package com.frontend.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class FrontendSecurityTest {

    @Mock
    private TokenStore tokenStore;

    @Mock
    private RestTemplate restTemplate;

    private CustomAuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        authenticationProvider = new CustomAuthenticationProvider(tokenStore, restTemplate);
    }

    @Test
    void testAuthenticateSuccess() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("juan", "password");
        String tokenResponse = "{\"token\":\"fake-token\"}";
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));

        Authentication result = authenticationProvider.authenticate(authentication);

        assertNotNull(result);
        assertEquals("juan", result.getName());
        verify(tokenStore).setToken("fake-token");
    }

    @Test
    void testAuthenticateFailureUnauthorized() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("juan", "wrongpassword");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(authentication);
        });
    }

    @Test
    void testAuthenticateFailureOtherException() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("juan", "password");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Error"));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(authentication);
        });
    }

    @Test
    void testSupports() {
        boolean result = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
        assertEquals(true, result);
    }

    @Test
    void testTokenStore() {
        TokenStore store = new TokenStore();
        store.setToken("test-token");
        assertEquals("test-token", store.getToken());
    }
}
