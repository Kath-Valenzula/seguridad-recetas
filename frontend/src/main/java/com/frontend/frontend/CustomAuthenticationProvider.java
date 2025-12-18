package com.frontend.frontend;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final TokenStore tokenStore;
    private final RestTemplate restTemplate;

    public CustomAuthenticationProvider(TokenStore tokenStore, RestTemplate restTemplate) {
        this.tokenStore = tokenStore;
        this.restTemplate = restTemplate;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();

        final MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", name);
        requestBody.add("password", password);

        final String token;
        try {
                final var responseEntity = restTemplate.postForEntity("http://localhost:8082/login", requestBody,
                    String.class);
            final var body = responseEntity.getBody();
            if (body == null) {
                throw new BadCredentialsException("Respuesta de autenticacion vacia");
            }
            token = body.replace("{\"token\":\"", "").replace("\"}", "");
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized ex) {
            throw new BadCredentialsException("Usuario o contrasena incorrectos");
        } catch (Exception ex) {
            throw new BadCredentialsException("Error al autenticar: " + ex.getMessage());
        }

        tokenStore.setToken(token);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(name, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
