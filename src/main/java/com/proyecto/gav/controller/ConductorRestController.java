package com.proyecto.gav.controller;


import com.proyecto.gav.DTO.ViajeConductorResponse;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.service.ConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conductor")
public class ConductorRestController {

    @Autowired
    private ConductorService conductorService;

    @GetMapping("/viajes")
    public ResponseEntity<?> getViajesDelConductor(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }

        String username = authentication.getName();
        Usuario conductor = conductorService.buscarPorNombreUsuario(username);

        if (conductor == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Conductor no encontrado"));
        }

        List<ViajeConductorResponse> viajes = conductorService.obtenerViajesAsignadosOEnCurso(conductor.getId());
        return ResponseEntity.ok(viajes);
    }

    @PostMapping("/viajes/{id}/iniciar")
    public ResponseEntity<?> iniciarViaje(@PathVariable String id) {
        try {
            conductorService.iniciarViaje(id);
            return ResponseEntity.ok(Map.of("mensaje", "Viaje iniciado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/viajes/{id}/finalizar")
    public ResponseEntity<?> finalizarViaje(@PathVariable String id) {
        try {
            conductorService.finalizarViaje(id);
            return ResponseEntity.ok(Map.of("mensaje", "Viaje finalizado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> getHistorialConductor(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autenticado"));
            }

            String nombreUsuario = authentication.getName();
            Usuario conductor = conductorService.buscarPorNombreUsuario(nombreUsuario);

            if (conductor == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Conductor no encontrado"));
            }

            List<ViajeConductorResponse> historial = conductorService.obtenerHistorialViajesDelConductor(conductor.getId());
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo obtener el historial: " + e.getMessage()));
        }
    }

}
