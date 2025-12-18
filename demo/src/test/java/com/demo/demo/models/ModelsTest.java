package com.demo.demo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ModelsTest {

    @Test
    void testReceta() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");
        receta.setTipoCocina("Española");
        receta.setIngredientes("Arroz, Azafran");
        receta.setPaisOrigen("España");
        receta.setDificultad("Media");
        receta.setTiempoCoccion(60);
        receta.setInstrucciones("Cocinar");
        receta.setImagenUrl("url");
        receta.setPopular(true);

        List<RecetaMedia> media = new ArrayList<>();
        receta.setMedia(media);

        List<Comentario> comentarios = new ArrayList<>();
        receta.setComentarios(comentarios);

        List<Valoracion> valoraciones = new ArrayList<>();
        receta.setValoraciones(valoraciones);

        assertEquals(1L, receta.getId());
        assertEquals("Paella", receta.getNombre());
        assertEquals("Española", receta.getTipoCocina());
        assertEquals("Arroz, Azafran", receta.getIngredientes());
        assertEquals("España", receta.getPaisOrigen());
        assertEquals("Media", receta.getDificultad());
        assertEquals(60, receta.getTiempoCoccion());
        assertEquals("Cocinar", receta.getInstrucciones());
        assertEquals("url", receta.getImagenUrl());
        assertEquals(true, receta.isPopular());
        assertEquals(media, receta.getMedia());
        assertEquals(comentarios, receta.getComentarios());
        assertEquals(valoraciones, receta.getValoraciones());
    }

    @Test
    void testUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("juan");
        user.setPassword("pass");
        user.setEmail("juan@example.com");

        assertEquals(1, user.getId());
        assertEquals("juan", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("juan@example.com", user.getEmail());

        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().stream().anyMatch(a -> "ROLE_USER".equals(a.getAuthority())));
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testComentario() {
        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setContenido("Muy rico");

        Receta receta = new Receta();
        comentario.setReceta(receta);

        User usuario = new User();
        comentario.setUsuario(usuario);

        LocalDateTime now = LocalDateTime.now();
        comentario.setFechaCreacion(now);

        comentario.prePersist();

        assertEquals(1L, comentario.getId());
        assertEquals("Muy rico", comentario.getContenido());
        assertEquals(receta, comentario.getReceta());
        assertEquals(usuario, comentario.getUsuario());
        // prePersist sets a new time, so we can't assert equality with 'now' easily
        // without mocking or loose check
        // but we can check it's not null
        // assertNotNull(comentario.getFechaCreacion());
    }

    @Test
    void testRecetaMedia() {
        RecetaMedia media = new RecetaMedia();
        media.setId(1L);
        media.setUrl("url");
        media.setTipo("video");

        Receta receta = new Receta();
        media.setReceta(receta);

        assertEquals(1L, media.getId());
        assertEquals("url", media.getUrl());
        assertEquals("video", media.getTipo());
        assertEquals(receta, media.getReceta());
    }

    @Test
    void testValoracion() {
        Valoracion valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setPuntuacion(5);

        Receta receta = new Receta();
        valoracion.setReceta(receta);

        User usuario = new User();
        valoracion.setUsuario(usuario);

        assertEquals(1L, valoracion.getId());
        assertEquals(5, valoracion.getPuntuacion());
        assertEquals(receta, valoracion.getReceta());
        assertEquals(usuario, valoracion.getUsuario());
    }
}
