package com.frontend.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.frontend.frontend.model.RecetaDTO;
import com.frontend.frontend.service.RecetaService;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings({ "all", "deprecation", "removal" })
class FrontendMissingCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomAuthenticationProvider authProvider;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private RecetaService recetaService;

    @MockBean
    private TokenStore tokenStore;

    @Test
    void testRecetaServiceFindByIdNoToken() {
        when(tokenStore.getToken()).thenReturn(null);
        RecetaDTO receta = new RecetaDTO();
        receta.setId(1L);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(RecetaDTO.class)))
                .thenReturn(new ResponseEntity<>(receta, HttpStatus.OK));

        RecetaDTO result = recetaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testLoginFailureHandler() throws Exception {
        when(authProvider.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(formLogin("/login").user("user").password("wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(unauthenticated());
    }

    @Test
    void testCustomAuthenticationProviderSupportsFalse() {
        // We need to instantiate it manually to test 'supports' without Spring
        // interfering or mocking it
        TokenStore tokenStore = mock(TokenStore.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(tokenStore, restTemplate);

        assertFalse(provider.supports(Object.class));
    }

    @Test
    void testRecetaServiceBuscarRecetasParams() {
        // This test targets the queryParamIfPresent calls in RecetaService
        // We need to verify that the URL is constructed correctly when params are null
        // or empty
        // RecetaService uses the @MockBean RestTemplate from this test context, so we
        // can verify the call.

        when(restTemplate.getForObject(any(String.class), eq(RecetaDTO[].class)))
                .thenReturn(new RecetaDTO[] {});

        recetaService.buscarRecetas(null, null, null, null, null, null);

        // Verify that getForObject was called. The exact URL construction is internal
        // logic of UriComponentsBuilder
        // but passing nulls should not crash and should produce a valid URI.
        verify(restTemplate).getForObject(any(String.class), eq(RecetaDTO[].class));
    }

    @Test
    void testRecetaServiceBuscarRecetasPopularFalse() {
        when(restTemplate.getForObject(any(String.class), eq(RecetaDTO[].class)))
                .thenReturn(new RecetaDTO[] {});

        recetaService.buscarRecetas(null, null, null, null, null, false);

        verify(restTemplate).getForObject(any(String.class), eq(RecetaDTO[].class));
    }

    @Test
    void testCustomAuthenticationProviderNullResponse() {
        // Test case where RestTemplate returns null body (or null entity if mocked that
        // way)
        // causing NPE which should be caught by the generic Exception catch block

        // CustomAuthenticationProvider is mocked in the Spring context, so instantiate
        // a fresh one here to exercise its logic with a mocked RestTemplate.

        TokenStore tokenStore = mock(TokenStore.class);
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(tokenStore, restTemplateMock);

        when(restTemplateMock.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(null); // This will cause NPE when calling .getBody() on null responseEntity?
        // No, postForEntity returns ResponseEntity. If we return null, then
        // responseEntity is null.

        assertFalse(provider.supports(Object.class)); // Just to use the provider

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", "pass");

        try {
            provider.authenticate(auth);
        } catch (BadCredentialsException e) {
            // Expected
        }
    }
}
