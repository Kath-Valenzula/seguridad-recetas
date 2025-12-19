package com.frontend.frontend.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.RecetaDTO;

@Service
@SuppressWarnings("null")
public class RecetaService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String BUSCAR_SEGMENT = "buscar";
    private static final String MEDIA_SEGMENT = "media";
    private static final String COMENTARIOS_SEGMENT = "comentarios";
    private static final String VALORACIONES_SEGMENT = "valoraciones";
    private static final String USER_ID_PARAM = "userId";

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8082/api/recetas";
    private final TokenStore tokenStore;

    public RecetaService(TokenStore tokenStore, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tokenStore = tokenStore;
    }

    public List<RecetaDTO> buscarRecetas(String nombre, String tipoCocina, String ingredientes, String paisOrigen,
            String dificultad, Boolean popular) {

        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
            .pathSegment(BUSCAR_SEGMENT)
                .queryParamIfPresent("nombre", Optional.ofNullable(nombre))
                .queryParamIfPresent("tipoCocina", Optional.ofNullable(tipoCocina))
                .queryParamIfPresent("ingredientes", Optional.ofNullable(ingredientes))
                .queryParamIfPresent("paisOrigen", Optional.ofNullable(paisOrigen))
                .queryParamIfPresent("dificultad", Optional.ofNullable(dificultad))
                .queryParamIfPresent("popular", Optional.ofNullable(popular))
                .toUriString();
        RecetaDTO[] recetas = restTemplate.getForObject(uri, RecetaDTO[].class);

        return recetas == null ? List.of() : Arrays.asList(recetas);
    }

    public RecetaDTO findById(Long id) {
        String uri = BASE_URL + "/" + id;
        String token = tokenStore.getToken();

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<RecetaDTO> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                RecetaDTO.class);

        return response.getBody();
    }

    public void addMedia(Long recetaId, List<com.frontend.frontend.model.RecetaMediaDTO> media) {
        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .pathSegment(String.valueOf(recetaId))
                .pathSegment(MEDIA_SEGMENT)
                .toUriString();
        String token = tokenStore.getToken();
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        }
        headers.set(CONTENT_TYPE_HEADER, APPLICATION_JSON);

        HttpEntity<List<com.frontend.frontend.model.RecetaMediaDTO>> entity = new HttpEntity<>(media, headers);
        restTemplate.postForEntity(uri, entity, Void.class);
    }

    public void addComment(Long recetaId, String contenido, Long userId) {
        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
            .pathSegment(String.valueOf(recetaId))
            .pathSegment(COMENTARIOS_SEGMENT)
                .queryParam(USER_ID_PARAM, userId)
                .toUriString();
        String token = tokenStore.getToken();
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        }
        headers.set(CONTENT_TYPE_HEADER, APPLICATION_JSON);

        com.frontend.frontend.model.ComentarioDTO comentario = new com.frontend.frontend.model.ComentarioDTO(contenido);
        HttpEntity<com.frontend.frontend.model.ComentarioDTO> entity = new HttpEntity<>(comentario, headers);
        restTemplate.postForEntity(uri, entity, Void.class);
    }

    public void addRating(Long recetaId, Integer puntuacion, Long userId) {
        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
            .pathSegment(String.valueOf(recetaId))
            .pathSegment(VALORACIONES_SEGMENT)
                .queryParam(USER_ID_PARAM, userId)
                .toUriString();
        String token = tokenStore.getToken();
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        }
        headers.set(CONTENT_TYPE_HEADER, APPLICATION_JSON);

        com.frontend.frontend.model.ValoracionDTO valoracion = new com.frontend.frontend.model.ValoracionDTO(
                puntuacion);
        HttpEntity<com.frontend.frontend.model.ValoracionDTO> entity = new HttpEntity<>(valoracion, headers);
        restTemplate.postForEntity(uri, entity, Void.class);
    }
}
