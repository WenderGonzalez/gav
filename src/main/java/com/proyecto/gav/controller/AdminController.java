package com.proyecto.gav.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/inicio")
    public String mostrarInicioAdmin(Model model) {

        String powerBiUrl = "https://app.powerbi.com/view?r=eyJrIjoiNzY5OWQwY2UtN2IyZC00NjY1LWEyMmMtNDhjYzJjYjNiNTUzIiwidCI6IjlkMTJiZjNmLWU0ZjYtNDdhYi05MTJmLTFhMmYwZmM0OGFhNCIsImMiOjR9";
        model.addAttribute("powerBiUrl", powerBiUrl);
        return "adminInicio";
    }

}
