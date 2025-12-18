package com.frontend.frontend.service;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.RecetaDTO;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class RecetaService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8082/api/recetas";
    private final TokenStore tokenStore;

    public RecetaService(TokenStore tokenStore, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tokenStore = tokenStore;
    }

    public List<RecetaDTO> buscarRecetas(String nombre, String tipoCocina, String ingredientes, String paisOrigen,
            String dificultad, Boolean popular) {

        String uri = UriComponentsBuilder.fromUriString(baseUrl + "/buscar")
                .queryParamIfPresent("nombre", Optional.ofNullable(nombre))
                .queryParamIfPresent("tipoCocina", Optional.ofNullable(tipoCocina))
                .queryParamIfPresent("ingredientes", Optional.ofNullable(ingredientes))
                .queryParamIfPresent("paisOrigen", Optional.ofNullable(paisOrigen))
                .queryParamIfPresent("dificultad", Optional.ofNullable(dificultad))
                .queryParamIfPresent("popular", Optional.ofNullable(popular))
                .toUriString();
        RecetaDTO[] recetas = restTemplate.getForObject(uri, RecetaDTO[].class);

        return Arrays.asList(recetas);
    }

    public RecetaDTO findById(Long id) {
        String uri = baseUrl + "/" + id;
        String token = tokenStore.getToken();

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
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
        String uri = baseUrl + "/" + recetaId + "/media";
        String token = tokenStore.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        HttpEntity<List<com.frontend.frontend.model.RecetaMediaDTO>> entity = new HttpEntity<>(media, headers);
        restTemplate.postForEntity(uri, entity, Void.class);
    }

    public void addComment(Long recetaId, String contenido, Long userId) {
        String uri = baseUrl + "/" + recetaId + "/comentarios?userId=" + userId;
        String token = tokenStore.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        com.frontend.frontend.model.ComentarioDTO comentario = new com.frontend.frontend.model.ComentarioDTO(contenido);
        HttpEntity<com.frontend.frontend.model.ComentarioDTO> entity = new HttpEntity<>(comentario, headers);
        restTemplate.postForEntity(uri, entity, Void.class);
    }

    public void addRating(Long recetaId, Integer puntuacion, Long userId) {
        String uri = baseUrl + "/" + recetaId + "/valoraciones?userId=" + userId;
        String token = tokenStore.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        com.frontend.frontend.model.ValoracionDTO valoracion = new com.frontend.frontend.model.ValoracionDTO(
                puntuacion);
        HttpEntity<com.frontend.frontend.model.ValoracionDTO> entity = new HttpEntity<>(valoracion, headers);
        restTemplate.postForEntity(uri, entity, Void.class);
    }
}
