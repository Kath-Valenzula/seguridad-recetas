package com.demo.demo.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DtosTest {

    @Test
    void testRecetaDTO() {
        RecetaDTO dto = new RecetaDTO();
        dto.setNombre("Paella");
        dto.setTipoCocina("Española");
        dto.setIngredientes("Arroz");
        dto.setPaisOrigen("España");
        dto.setDificultad("Media");
        dto.setTiempoCoccion(60);
        dto.setInstrucciones("Cocinar");
        dto.setImagenUrl("url");
        dto.setPopular(true);

        assertEquals("Paella", dto.getNombre());
        assertEquals("Española", dto.getTipoCocina());
        assertEquals("Arroz", dto.getIngredientes());
        assertEquals("España", dto.getPaisOrigen());
        assertEquals("Media", dto.getDificultad());
        assertEquals(60, dto.getTiempoCoccion());
        assertEquals("Cocinar", dto.getInstrucciones());
        assertEquals("url", dto.getImagenUrl());
        assertTrue(dto.isPopular());
    }

    @Test
    void testComentarioDTO() {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setContenido("Rico");

        assertEquals("Rico", dto.getContenido());
    }

    @Test
    void testRecetaMediaDTO() {
        RecetaMediaDTO dto = new RecetaMediaDTO();
        dto.setUrl("url");
        dto.setTipo("video");

        assertEquals("url", dto.getUrl());
        assertEquals("video", dto.getTipo());
    }

    @Test
    void testValoracionDTO() {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setPuntuacion(5);

        assertEquals(5, dto.getPuntuacion());
    }

    @Test
    void testRecetaDetalleDTO() {
        RecetaDetalleDTO dto = new RecetaDetalleDTO();
        dto.setId(1L);
        dto.setNombre("Paella");
        dto.setTipoCocina("Española");
        dto.setIngredientes("Arroz");
        dto.setPaisOrigen("España");
        dto.setDificultad("Media");
        dto.setTiempoCoccion(60);
        dto.setInstrucciones("Cocinar");
        dto.setImagenUrl("url");
        dto.setPopular(true);

        List<RecetaDetalleDTO.MediaDTO> media = new ArrayList<>();
        dto.setMedia(media);

        List<RecetaDetalleDTO.ComentarioDetalleDTO> comentarios = new ArrayList<>();
        dto.setComentarios(comentarios);

        List<RecetaDetalleDTO.ValoracionDetalleDTO> valoraciones = new ArrayList<>();
        dto.setValoraciones(valoraciones);

        assertEquals(1L, dto.getId());
        assertEquals("Paella", dto.getNombre());
        assertEquals("Española", dto.getTipoCocina());
        assertEquals("Arroz", dto.getIngredientes());
        assertEquals("España", dto.getPaisOrigen());
        assertEquals("Media", dto.getDificultad());
        assertEquals(60, dto.getTiempoCoccion());
        assertEquals("Cocinar", dto.getInstrucciones());
        assertEquals("url", dto.getImagenUrl());
        assertTrue(dto.isPopular());
        assertEquals(media, dto.getMedia());
        assertEquals(comentarios, dto.getComentarios());
        assertEquals(valoraciones, dto.getValoraciones());
    }

    @Test
    void testRecetaDetalleInnerClasses() {
        RecetaDetalleDTO.MediaDTO media = new RecetaDetalleDTO.MediaDTO(1L, "url", "video");
        assertEquals(1L, media.getId());
        assertEquals("url", media.getUrl());
        assertEquals("video", media.getTipo());

        media.setId(2L);
        media.setUrl("url2");
        media.setTipo("img");
        assertEquals(2L, media.getId());
        assertEquals("url2", media.getUrl());
        assertEquals("img", media.getTipo());

        LocalDateTime now = LocalDateTime.now();
        RecetaDetalleDTO.ComentarioDetalleDTO comentario = new RecetaDetalleDTO.ComentarioDetalleDTO(1L, "content",
                "user", now);
        assertEquals(1L, comentario.getId());
        assertEquals("content", comentario.getContenido());
        assertEquals("user", comentario.getNombreUsuario());
        assertEquals(now, comentario.getFechaCreacion());

        comentario.setId(2L);
        comentario.setContenido("content2");
        comentario.setNombreUsuario("user2");
        LocalDateTime now2 = LocalDateTime.now();
        comentario.setFechaCreacion(now2);
        assertEquals(2L, comentario.getId());
        assertEquals("content2", comentario.getContenido());
        assertEquals("user2", comentario.getNombreUsuario());
        assertEquals(now2, comentario.getFechaCreacion());

        RecetaDetalleDTO.ValoracionDetalleDTO valoracion = new RecetaDetalleDTO.ValoracionDetalleDTO(1L, 5, "user");
        assertEquals(1L, valoracion.getId());
        assertEquals(5, valoracion.getPuntuacion());
        assertEquals("user", valoracion.getNombreUsuario());

        valoracion.setId(2L);
        valoracion.setPuntuacion(4);
        valoracion.setNombreUsuario("user2");
        assertEquals(2L, valoracion.getId());
        assertEquals(4, valoracion.getPuntuacion());
        assertEquals("user2", valoracion.getNombreUsuario());
    }
}
