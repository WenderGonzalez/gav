package com.proyecto.gav.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConductorController {

    @GetMapping("/conductor/inicio")
    public String mostrarInicioConductor() {
        return "conductorInicio";
    }

}