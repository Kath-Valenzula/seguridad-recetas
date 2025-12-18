package com.demo.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.demo.demo.dto.ComentarioDTO;
import com.demo.demo.dto.RecetaDetalleDTO;
import com.demo.demo.dto.RecetaMediaDTO;
import com.demo.demo.dto.ValoracionDTO;
import com.demo.demo.models.Receta;
import com.demo.demo.service.RecetaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class RecetasControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecetaService recetaService;

    @InjectMocks
    private RecetasController recetasController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recetasController).build();
    }

    @Test
    void testHome() throws Exception {
        when(recetaService.listarPopulares()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recetas/home"))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscar() throws Exception {
        when(recetaService.buscar(any(), any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recetas/buscar")
                .param("nombre", "Paella"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetalle() throws Exception {
        RecetaDetalleDTO dto = new RecetaDetalleDTO();
        when(recetaService.obtenerDetallePorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/recetas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetalleNotFound() throws Exception {
        when(recetaService.obtenerDetallePorId(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/recetas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrear() throws Exception {
        Receta receta = new Receta();
        when(recetaService.guardar(any(Receta.class))).thenReturn(receta);

        mockMvc.perform(post("/api/recetas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receta)))
                .andExpect(status().isOk());
    }

    @Test
    void testAgregarMedia() throws Exception {
        RecetaMediaDTO dto = new RecetaMediaDTO();
        dto.setUrl("url");
        dto.setTipo("video");

        mockMvc.perform(post("/api/recetas/1/media")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(dto))))
                .andExpect(status().isOk());
    }

    @Test
    void testAgregarComentario() throws Exception {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setContenido("Rico");

        mockMvc.perform(post("/api/recetas/1/comentarios")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testAgregarValoracion() throws Exception {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setPuntuacion(5);

        mockMvc.perform(post("/api/recetas/1/valoraciones")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
