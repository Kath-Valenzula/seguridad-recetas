package com.frontend.frontend.service;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.UsuarioDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@SuppressWarnings({ "null", "catching" })
public class UserService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8082/api/usuarios";
    private final TokenStore tokenStore;

    public UserService(TokenStore tokenStore, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tokenStore = tokenStore;
    }

    public UsuarioDTO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Assuming there is an endpoint to find by username or "me"
        // Trying to find by username as it is more standard if "me" doesn't exist and
        // we have the username
        String uri = baseUrl + "/username/" + username;

        // Fallback or alternative: if the backend supports /me
        // String uri = baseUrl + "/me";

        String token = tokenStore.getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    UsuarioDTO.class);
            return response.getBody();
        } catch (org.springframework.web.client.RestClientException e) {
            System.out.println("Error fetching user: " + e.getMessage());
            return null;
        }
    }
}
