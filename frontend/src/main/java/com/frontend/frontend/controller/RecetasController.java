package com.frontend.frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.frontend.frontend.model.RecetaDTO;
import com.frontend.frontend.service.RecetaService;

@Controller
@RequestMapping("/recetas")
public class RecetasController {

    private final RecetaService recetaService;

    public RecetasController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public String listarRecetas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String ingredientes,
            @RequestParam(required = false) String paisOrigen,
            @RequestParam(required = false) String dificultad,
            Model model) {

        List<RecetaDTO> recetas = recetaService.buscarRecetas(nombre, tipoCocina, ingredientes, paisOrigen, dificultad,
                null);
        model.addAttribute("recetas", recetas);
        return "recetas";
    }

    @GetMapping("/{id}")
    public String detalleReceta(@PathVariable Long id, Model model) {

        RecetaDTO receta = recetaService.findById(id);

        model.addAttribute("receta", receta); // Add the whole object for easier access
        model.addAttribute("nombre", receta.getNombre());
        model.addAttribute("ingredientes", receta.getIngredientes().split(",\\s*")); // convertir string en lista
        model.addAttribute("instrucciones", receta.getInstrucciones().split("\\;\\s*")); // separar por puntos si
                                                                                         // quieres pasos
        model.addAttribute("tiempo", receta.getTiempoPreparacion() + " minutos");
        model.addAttribute("dificultad", receta.getDificultad());
        model.addAttribute("imagenUrl", receta.getImagenUrl());
        model.addAttribute("popular", receta.getPopular());

        model.addAttribute("media", receta.getMedia());
        model.addAttribute("comentarios", receta.getComentarios());
        model.addAttribute("valoraciones", receta.getValoraciones());

        return "receta-detalle";
    }

    @PostMapping("/{id}/media")
    public String agregarMedia(@PathVariable Long id, @RequestParam String url, @RequestParam String tipo) {
        com.frontend.frontend.model.RecetaMediaDTO mediaDTO = new com.frontend.frontend.model.RecetaMediaDTO(url, tipo);
        recetaService.addMedia(id, java.util.Collections.singletonList(mediaDTO));
        return "redirect:/recetas/" + id;
    }

    @PostMapping("/{id}/comentarios")
    public String agregarComentario(@PathVariable Long id, @RequestParam String contenido) {
        Long userId = 1L;
        recetaService.addComment(id, contenido, userId);
        return "redirect:/recetas/" + id;
    }

    @PostMapping("/{id}/valoraciones")
    public String agregarValoracion(@PathVariable Long id, @RequestParam Integer puntuacion) {
        Long userId = 1L;
        recetaService.addRating(id, puntuacion, userId);
        return "redirect:/recetas/" + id;
    }

}
