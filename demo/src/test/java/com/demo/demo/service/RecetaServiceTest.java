package com.demo.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demo.demo.dto.ComentarioDTO;
import com.demo.demo.dto.RecetaDTO;
import com.demo.demo.dto.RecetaDetalleDTO;
import com.demo.demo.dto.RecetaMediaDTO;
import com.demo.demo.dto.ValoracionDTO;
import com.demo.demo.models.Comentario;
import com.demo.demo.models.Receta;
import com.demo.demo.models.RecetaMedia;
import com.demo.demo.models.User;
import com.demo.demo.models.Valoracion;
import com.demo.demo.repository.ComentarioRepository;
import com.demo.demo.repository.RecetaMediaRepository;
import com.demo.demo.repository.RecetaRepository;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.repository.ValoracionRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;
    @Mock
    private RecetaMediaRepository recetaMediaRepository;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private ValoracionRepository valoracionRepository;
    @Mock
    private UserRepository userRepository;

    private RecetaService recetaService;

    @BeforeEach
    void setUp() {
        recetaService = new RecetaService(recetaRepository, recetaMediaRepository, comentarioRepository,
                valoracionRepository, userRepository);
    }

    @Test
    void testListarTodas() {
        Receta receta = new Receta();
        receta.setId(1L);
        when(recetaRepository.findAll()).thenReturn(Arrays.asList(receta));

        List<RecetaDTO> result = recetaService.listarTodas();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testListarPopulares() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setPopular(true);
        when(recetaRepository.findByPopularTrue()).thenReturn(Arrays.asList(receta));

        List<RecetaDTO> result = recetaService.listarPopulares();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isPopular());
    }

    @Test
    void testObtenerPorId() {
        Receta receta = new Receta();
        receta.setId(1L);
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        Optional<Receta> result = recetaService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGuardar() {
        Receta receta = new Receta();
        when(recetaRepository.save(receta)).thenReturn(receta);

        Receta result = recetaService.guardar(receta);

        assertEquals(receta, result);
    }

    @Test
    void testBuscar() {
        Receta receta = new Receta();
        receta.setId(1L);
        List<Receta> recetas = Arrays.asList(receta);

        // Case 1: Popular
        when(recetaRepository.findByPopularTrue()).thenReturn(recetas);
        List<RecetaDTO> result = recetaService.buscar(null, null, null, null, null, true);
        assertEquals(1, result.size());

        // Case 2: Nombre
        when(recetaRepository.findByNombreContainingIgnoreCase("Paella")).thenReturn(recetas);
        result = recetaService.buscar("Paella", null, null, null, null, null);
        assertEquals(1, result.size());

        // Case 3: TipoCocina
        when(recetaRepository.findByTipoCocinaContainingIgnoreCase("Española")).thenReturn(recetas);
        result = recetaService.buscar(null, "Española", null, null, null, null);
        assertEquals(1, result.size());

        // Case 4: Ingredientes
        when(recetaRepository.findByIngredientesContainingIgnoreCase("Arroz")).thenReturn(recetas);
        result = recetaService.buscar(null, null, "Arroz", null, null, null);
        assertEquals(1, result.size());

        // Case 5: Pais
        when(recetaRepository.findByPaisOrigenContainingIgnoreCase("España")).thenReturn(recetas);
        result = recetaService.buscar(null, null, null, "España", null, null);
        assertEquals(1, result.size());

        // Case 6: Dificultad
        when(recetaRepository.findByDificultadContainingIgnoreCase("Media")).thenReturn(recetas);
        result = recetaService.buscar(null, null, null, null, "Media", null);
        assertEquals(1, result.size());

        // Case 7: All null (findAll)
        when(recetaRepository.findAll()).thenReturn(recetas);
        result = recetaService.buscar(null, null, null, null, null, null);
        assertEquals(1, result.size());

        // Case 8: Empty strings (should fall through to next or findAll)
        result = recetaService.buscar("", "", "", "", "", null);
        assertEquals(1, result.size());
    }

    @Test
    void testAgregarMedia() {
        Receta receta = new Receta();
        receta.setId(1L);
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        RecetaMediaDTO dto = new RecetaMediaDTO();
        dto.setUrl("url");
        dto.setTipo("video");

        recetaService.agregarMedia(1L, Arrays.asList(dto));

        verify(recetaMediaRepository, times(1)).save(any(RecetaMedia.class));
    }

    @Test
    void testAgregarMediaRecetaNotFound() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());
        List<RecetaMediaDTO> dtos = Collections.emptyList();
        assertThrows(RuntimeException.class, () -> recetaService.agregarMedia(1L, dtos));
    }

    @Test
    void testAgregarComentario() {
        Receta receta = new Receta();
        receta.setId(1L);
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ComentarioDTO dto = new ComentarioDTO();
        dto.setContenido("Rico");

        recetaService.agregarComentario(1L, 1, dto);

        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    @Test
    void testAgregarComentarioRecetaNotFound() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());
        ComentarioDTO dto = new ComentarioDTO();
        assertThrows(RuntimeException.class, () -> recetaService.agregarComentario(1L, 1, dto));
    }

    @Test
    void testAgregarComentarioUserNotFound() {
        Receta receta = new Receta();
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        ComentarioDTO dto = new ComentarioDTO();
        assertThrows(RuntimeException.class, () -> recetaService.agregarComentario(1L, 1, dto));
    }

    @Test
    void testAgregarValoracion() {
        Receta receta = new Receta();
        receta.setId(1L);
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ValoracionDTO dto = new ValoracionDTO();
        dto.setPuntuacion(5);

        recetaService.agregarValoracion(1L, 1, dto);

        verify(valoracionRepository, times(1)).save(any(Valoracion.class));
    }

    @Test
    void testAgregarValoracionRecetaNotFound() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());
        ValoracionDTO dto = new ValoracionDTO();
        assertThrows(RuntimeException.class, () -> recetaService.agregarValoracion(1L, 1, dto));
    }

    @Test
    void testAgregarValoracionUserNotFound() {
        Receta receta = new Receta();
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        ValoracionDTO dto = new ValoracionDTO();
        assertThrows(RuntimeException.class, () -> recetaService.agregarValoracion(1L, 1, dto));
    }

    @Test
    void testObtenerDetallePorId() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");

        RecetaMedia media = new RecetaMedia();
        media.setId(1L);
        media.setUrl("url");
        media.setTipo("video");
        receta.setMedia(Arrays.asList(media));

        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setContenido("Rico");
        comentario.setFechaCreacion(LocalDateTime.now());
        User user = new User();
        user.setUsername("juan");
        comentario.setUsuario(user);
        receta.setComentarios(Arrays.asList(comentario));

        Valoracion valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setPuntuacion(5);
        valoracion.setUsuario(user);
        receta.setValoraciones(Arrays.asList(valoracion));

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        RecetaDetalleDTO result = recetaService.obtenerDetallePorId(1L);

        assertEquals(1L, result.getId());
        assertEquals("Paella", result.getNombre());
        assertEquals(1, result.getMedia().size());
        assertEquals(1, result.getComentarios().size());
        assertEquals(1, result.getValoraciones().size());
    }

    @Test
    void testObtenerDetallePorIdNotFound() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> recetaService.obtenerDetallePorId(1L));
    }
}
