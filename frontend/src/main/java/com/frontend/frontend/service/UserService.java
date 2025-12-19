package com.frontend.frontend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.UsuarioDTO;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BASE_URL = "http://localhost:8082/api/usuarios";

    private final RestTemplate restTemplate;
    private final TokenStore tokenStore;

    public UserService(TokenStore tokenStore, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tokenStore = tokenStore;
    }

    public UsuarioDTO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
            .pathSegment("username")
            .pathSegment(username)
            .toUriString();

        String token = tokenStore.getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    UsuarioDTO.class);
            return response.getBody();
        } catch (org.springframework.web.client.RestClientException e) {
            LOGGER.warn("Error fetching user", e);
            return null;
        }
    }
}
