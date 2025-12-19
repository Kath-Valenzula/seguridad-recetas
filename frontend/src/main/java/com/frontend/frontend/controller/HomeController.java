package com.frontend.frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.frontend.frontend.TokenStore;
import com.frontend.frontend.model.RecetaDTO;
import com.frontend.frontend.service.RecetaService;

@Controller
public class HomeController {

    private final TokenStore tokenStore;
    private final RecetaService recetaService;

    public HomeController(TokenStore tokenStore, RecetaService recetaService) {
        this.tokenStore = tokenStore;
        this.recetaService = recetaService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        return renderHome(model);
    }

    @GetMapping("/")
    public String inicio(Model model) {
        return renderHome(model);
    }

    @PostMapping("/logout")
    public String logout() {
        tokenStore.setToken(null);
        return "redirect:/login";
    }

    private String renderHome(Model model) {
        List<RecetaDTO> recetasPopulares = recetaService.buscarRecetas(null, null, null, null, null, true);
        List<RecetaDTO> recetasRecientes = recetaService.buscarRecetas(null, null, null, null, null, null);

        model.addAttribute("recetasPopulares", recetasPopulares);
        model.addAttribute("recetasRecientes", recetasRecientes);

        return "home";
    }

}
