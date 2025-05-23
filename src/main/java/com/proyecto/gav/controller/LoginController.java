package com.proyecto.gav.controller;

import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/login")
    public String mostrarFormularioLogin(@RequestParam(value = "logout", required = false) String logout, Model model) {
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente");
        }
        return "login";
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute Usuario cliente, Model model) {

        // Validación de existencia de nombre de usuario, email y número de documento
        if (clienteService.nombreUsuarioExiste(cliente.getNombreUsuario())) {
            model.addAttribute("error", "Nombre de usuario ya está en uso.");
            return "register";
        }

        if (clienteService.emailExiste(cliente.getEmail())) {
            model.addAttribute("error", "Correo electrónico ya está en uso.");
            return "register";
        }

        if (clienteService.numeroDocumentoExiste(cliente.getNumeroDocumento())) {
            model.addAttribute("error", "Número de documento ya está en uso.");
            return "register";
        }

        if (clienteService.telefonoExiste(cliente.getTelefono())){
            model.addAttribute("error","Numero de telefono ya esta en uso");
            return  "register";
        }

        // Registrar cliente con todos los datos
        clienteService.registrarCliente(cliente);

        return "redirect:/login";
    }
}
