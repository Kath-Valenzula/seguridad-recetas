package com.frontend.frontend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class FrontendModelsTest {

    @Test
    void testComentarioDTO() {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(1L);
        dto.setContenido("content");
        dto.setNombreUsuario("user");
        dto.setFechaCreacion("date");

        assertEquals(1L, dto.getId());
        assertEquals("content", dto.getContenido());
        assertEquals("user", dto.getNombreUsuario());
        assertEquals("date", dto.getFechaCreacion());

        ComentarioDTO dto2 = new ComentarioDTO("content2");
        assertEquals("content2", dto2.getContenido());
    }

    @Test
    void testRecetaDTO() {
        RecetaDTO dto = new RecetaDTO();
        dto.setId(1L);
        dto.setNombre("Paella");
        dto.setTipoCocina("Española");
        dto.setIngredientes("Arroz");
        dto.setPaisOrigen("España");
        dto.setDificultad("Media");
        dto.setTiempoPreparacion(60);
        dto.setInstrucciones("Cocinar");
        dto.setImagenUrl("url");
        dto.setPopular(true);

        List<RecetaMediaDTO> media = new ArrayList<>();
        dto.setMedia(media);

        List<ComentarioDTO> comentarios = new ArrayList<>();
        dto.setComentarios(comentarios);

        List<ValoracionDTO> valoraciones = new ArrayList<>();
        dto.setValoraciones(valoraciones);

        assertEquals(1L, dto.getId());
        assertEquals("Paella", dto.getNombre());
        assertEquals("Española", dto.getTipoCocina());
        assertEquals("Arroz", dto.getIngredientes());
        assertEquals("España", dto.getPaisOrigen());
        assertEquals("Media", dto.getDificultad());
        assertEquals(60, dto.getTiempoPreparacion());
        assertEquals("Cocinar", dto.getInstrucciones());
        assertEquals("url", dto.getImagenUrl());
        assertTrue(dto.getPopular());
        assertEquals(media, dto.getMedia());
        assertEquals(comentarios, dto.getComentarios());
        assertEquals(valoraciones, dto.getValoraciones());

        assertEquals("Paella - 60 min", dto.toString());
    }

    @Test
    void testRecetaMediaDTO() {
        RecetaMediaDTO dto = new RecetaMediaDTO();
        dto.setId(1L);
        dto.setUrl("url");
        dto.setTipo("video");

        assertEquals(1L, dto.getId());
        assertEquals("url", dto.getUrl());
        assertEquals("video", dto.getTipo());

        RecetaMediaDTO dto2 = new RecetaMediaDTO("url2", "img");
        assertEquals("url2", dto2.getUrl());
        assertEquals("img", dto2.getTipo());
    }

    @Test
    void testUsuarioDTO() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(1L);
        dto.setUsername("juan");
        dto.setEmail("juan@example.com");

        assertEquals(1L, dto.getId());
        assertEquals("juan", dto.getUsername());
        assertEquals("juan@example.com", dto.getEmail());
    }

    @Test
    void testValoracionDTO() {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(1L);
        dto.setPuntuacion(5);
        dto.setNombreUsuario("user");

        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getPuntuacion());
        assertEquals("user", dto.getNombreUsuario());

        ValoracionDTO dto2 = new ValoracionDTO(4);
        assertEquals(4, dto2.getPuntuacion());
    }
}
