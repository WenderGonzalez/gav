package com.proyecto.gav.controller;

import com.proyecto.gav.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @GetMapping("/cliente/inicio")
    public String mostrarInicioCliente() {

        return "clienteInicio";
    }



}
