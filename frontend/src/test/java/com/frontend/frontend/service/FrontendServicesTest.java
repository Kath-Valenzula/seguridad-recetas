package com.frontend.frontend.service;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.RecetaDTO;
import com.frontend.frontend.model.RecetaMediaDTO;
import com.frontend.frontend.model.UsuarioDTO;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class FrontendServicesTest {

    @Mock
    private TokenStore tokenStore;

    @Mock
    private RestTemplate restTemplate;

    private RecetaService recetaService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        recetaService = new RecetaService(tokenStore, restTemplate);
        userService = new UserService(tokenStore, restTemplate);
    }

    @Test
    void testBuscarRecetas() {
        RecetaDTO[] recetasArray = new RecetaDTO[1];
        recetasArray[0] = new RecetaDTO();
        when(restTemplate.getForObject(anyString(), eq(RecetaDTO[].class))).thenReturn(recetasArray);

        List<RecetaDTO> result = recetaService.buscarRecetas("Paella", "Española", "Arroz", "España", "Media", true);

        assertEquals(1, result.size());
        verify(restTemplate).getForObject(anyString(), eq(RecetaDTO[].class));
    }

    @Test
    void testFindById() {
        RecetaDTO receta = new RecetaDTO();
        receta.setId(1L);
        when(tokenStore.getToken()).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(RecetaDTO.class)))
                .thenReturn(new ResponseEntity<>(receta, HttpStatus.OK));

        RecetaDTO result = recetaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testAddMedia() {
        when(tokenStore.getToken()).thenReturn("token");
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        recetaService.addMedia(1L, Collections.singletonList(new RecetaMediaDTO()));

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void testAddComment() {
        when(tokenStore.getToken()).thenReturn("token");
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        recetaService.addComment(1L, "Comentario", 1L);

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void testAddRating() {
        when(tokenStore.getToken()).thenReturn("token");
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        recetaService.addRating(1L, 5, 1L);

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void testGetCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("juan");
        SecurityContextHolder.setContext(securityContext);

        when(tokenStore.getToken()).thenReturn("token");
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setUsername("juan");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(UsuarioDTO.class)))
                .thenReturn(new ResponseEntity<>(usuario, HttpStatus.OK));

        UsuarioDTO result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals("juan", result.getUsername());
    }

    @Test
    void testGetCurrentUserNoToken() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("juan");
        SecurityContextHolder.setContext(securityContext);

        when(tokenStore.getToken()).thenReturn(null);

        UsuarioDTO result = userService.getCurrentUser();

        assertNull(result);
    }

    @Test
    void testGetCurrentUserException() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("juan");
        SecurityContextHolder.setContext(securityContext);

        when(tokenStore.getToken()).thenReturn("token");
            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(UsuarioDTO.class)))
                .thenThrow(new RestClientException("Error"));

        UsuarioDTO result = userService.getCurrentUser();

        assertNull(result);
    }

    @Test
    void testFindByIdEmptyToken() {
        RecetaDTO receta = new RecetaDTO();
        receta.setId(1L);
        when(tokenStore.getToken()).thenReturn("");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(RecetaDTO.class)))
                .thenReturn(new ResponseEntity<>(receta, HttpStatus.OK));

        RecetaDTO result = recetaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetCurrentUserEmptyToken() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("juan");
        SecurityContextHolder.setContext(securityContext);

        when(tokenStore.getToken()).thenReturn("");

        UsuarioDTO result = userService.getCurrentUser();

        assertNull(result);
    }
}
