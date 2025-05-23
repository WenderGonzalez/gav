package com.proyecto.gav.config;

import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSetup implements CommandLineRunner {

    private final UsuarioService usuarioService;

    public AdminSetup(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        usuarioService.registrarAdmin();
    }
}