package com.proyecto.gav.service;

import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarAdmin() {
        if (usuarioRepository.existsByNombreUsuario("admin")) {
            return;
        }


        Usuario admin = new Usuario();
        admin.setNombreUsuario("admin");
        admin.setContrasena(passwordEncoder.encode("12345"));
        admin.setRol(Usuario.Rol.ADMIN);

        usuarioRepository.save(admin);

        System.out.println("Administrador creado con éxito");
    }
}