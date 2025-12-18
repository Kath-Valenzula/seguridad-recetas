package com.frontend.frontend.controller;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.RecetaDTO;
import com.frontend.frontend.service.RecetaService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class FrontendControllersTest {

        @Mock
        private RecetaService recetaService;

        @Mock
        private TokenStore tokenStore;

        @Mock
        private RestTemplate restTemplate;

        @InjectMocks
        private RecetasController recetasController;

        @InjectMocks
        private HomeController homeController;

        private AuthController authController;

        private MockMvc mockMvcRecetas;
        private MockMvc mockMvcHome;
        private MockMvc mockMvcAuth;

        @BeforeEach
        void setUp() {
                authController = new AuthController(restTemplate);
                recetasController = new RecetasController(recetaService);
                homeController = new HomeController(tokenStore, recetaService);

                mockMvcRecetas = MockMvcBuilders.standaloneSetup(recetasController)
                                .setViewResolvers(new org.springframework.web.servlet.view.InternalResourceViewResolver(
                                                "/WEB-INF/views/", ".html"))
                                .build();
                mockMvcHome = MockMvcBuilders.standaloneSetup(homeController)
                                .setViewResolvers(new org.springframework.web.servlet.view.InternalResourceViewResolver(
                                                "/WEB-INF/views/", ".html"))
                                .build();
                mockMvcAuth = MockMvcBuilders.standaloneSetup(authController)
                                .setViewResolvers(new org.springframework.web.servlet.view.InternalResourceViewResolver(
                                                "/WEB-INF/views/", ".html"))
                                .build();
        }

        // RecetasController Tests
        @Test
        void testListarRecetas() throws Exception {
                when(recetaService.buscarRecetas(any(), any(), any(), any(), any(), any()))
                                .thenReturn(Collections.emptyList());

                mockMvcRecetas.perform(get("/recetas"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("recetas"))
                                .andExpect(model().attributeExists("recetas"));
        }

        @Test
        void testDetalleReceta() throws Exception {
                RecetaDTO receta = new RecetaDTO();
                receta.setId(1L);
                receta.setNombre("Paella");
                receta.setIngredientes("Arroz, Azafran");
                receta.setInstrucciones("Paso 1; Paso 2");
                receta.setTiempoPreparacion(60);

                when(recetaService.findById(1L)).thenReturn(receta);

                mockMvcRecetas.perform(get("/recetas/1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("receta-detalle"))
                                .andExpect(model().attributeExists("receta"));
        }

        @Test
        void testAgregarMedia() throws Exception {
                mockMvcRecetas.perform(post("/recetas/1/media")
                                .param("url", "http://example.com/image.jpg")
                                .param("tipo", "FOTO"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/recetas/1"));

                verify(recetaService).addMedia(eq(1L), any());
        }

        @Test
        void testAgregarComentario() throws Exception {
                mockMvcRecetas.perform(post("/recetas/1/comentarios")
                                .param("contenido", "Muy rica"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/recetas/1"));

                verify(recetaService).addComment(eq(1L), eq("Muy rica"), anyLong());
        }

        @Test
        void testAgregarValoracion() throws Exception {
                mockMvcRecetas.perform(post("/recetas/1/valoraciones")
                                .param("puntuacion", "5"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/recetas/1"));

                verify(recetaService).addRating(eq(1L), eq(5), anyLong());
        }

        // HomeController Tests
        @Test
        void testHome() throws Exception {
                when(recetaService.buscarRecetas(any(), any(), any(), any(), any(), anyBoolean()))
                                .thenReturn(Collections.emptyList());
                when(recetaService.buscarRecetas(any(), any(), any(), any(), any(), eq(null)))
                                .thenReturn(Collections.emptyList());

                mockMvcHome.perform(get("/home"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("home"))
                                .andExpect(model().attributeExists("recetasPopulares"))
                                .andExpect(model().attributeExists("recetasRecientes"));
        }

        @Test
        void testInicio() throws Exception {
                when(recetaService.buscarRecetas(any(), any(), any(), any(), any(), anyBoolean()))
                                .thenReturn(Collections.emptyList());
                when(recetaService.buscarRecetas(any(), any(), any(), any(), any(), eq(null)))
                                .thenReturn(Collections.emptyList());

                mockMvcHome.perform(get("/"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("home"));
        }

        @Test
        void testLogout() throws Exception {
                mockMvcHome.perform(post("/logout"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));

                verify(tokenStore).setToken(null);
        }

        // AuthController Tests
        @Test
        void testLogin() throws Exception {
                mockMvcAuth.perform(get("/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("login"));
        }

        @Test
        void testRegisterGet() throws Exception {
                mockMvcAuth.perform(get("/register"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("register"));
        }

        @Test
        void testRegisterPostSuccess() throws Exception {
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                                .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

                mockMvcAuth.perform(post("/register")
                                .param("username", "juan")
                                .param("email", "juan@example.com")
                                .param("password", "password"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("register"))
                                .andExpect(model().attributeExists("message"));
        }

        @Test
        void testRegisterPostError() throws Exception {
                when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                                .thenThrow(new RestClientException("Error"));

                mockMvcAuth.perform(post("/register")
                                .param("username", "juan")
                                .param("email", "juan@example.com")
                                .param("password", "password"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("register"))
                                .andExpect(model().attributeExists("message"));
        }
}
